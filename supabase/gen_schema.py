#!/usr/bin/env python3
"""Generate the Supabase Postgres DDL + Phase-0 RLS from the Room @Entity Kotlin data classes.

Run from the repo root:  python3 supabase/gen_schema.py
Re-run whenever entities change to regenerate supabase/schema.sql and supabase/rls_phase0.sql.
Only constructor-stored fields become columns; computed `val x get() = ...` properties are ignored.
"""
import os, re, sys, glob

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ENTITY_DIR = os.path.join(ROOT, "app/src/main/java/com/alhadi/cmms/data/entity")
OUT_DIR = os.path.join(ROOT, "supabase")

TYPE_MAP = {
    "Long": "bigint",
    "Int": "integer",
    "Double": "double precision",
    "Float": "double precision",
    "Boolean": "boolean",
    "String": "text",
}


def kotlin_type_to_pg(ktype):
    return TYPE_MAP.get(ktype.rstrip("?"), "text")


def split_params(s):
    parts, depth, cur = [], 0, ""
    for ch in s:
        if ch in "(<[":
            depth += 1
        elif ch in ")>]":
            depth -= 1
        if ch == "," and depth == 0:
            parts.append(cur); cur = ""
        else:
            cur += ch
    if cur.strip():
        parts.append(cur)
    return parts


def extract_constructor(text):
    m = re.search(r"data class \w+\s*\(", text)
    if not m:
        return None
    i = m.end() - 1
    depth, start = 0, i + 1
    for j in range(i, len(text)):
        if text[j] == "(":
            depth += 1
        elif text[j] == ")":
            depth -= 1
            if depth == 0:
                return text[start:j]
    return None


def parse_entity(path):
    text = open(path).read()
    tm = re.search(r'tableName\s*=\s*"([a-z_]+)"', text)
    if not tm:
        return None
    ctor = extract_constructor(text)
    if ctor is None:
        return None
    cols = []
    for chunk in split_params(ctor):
        fm = re.search(r"\bval\s+(\w+)\s*:\s*([\w<>.]+\??)", chunk)
        if not fm:
            continue
        name, ktype = fm.group(1), fm.group(2)
        cols.append((name, kotlin_type_to_pg(ktype), ktype.endswith("?"), name == "id"))
    return tm.group(1), cols


def main():
    schema = [
        "-- Al-Hadi CMMS — Supabase Postgres schema (generated from Room entities).",
        "-- Phase 0. Apply in the Supabase SQL editor. ids are client-supplied (match Room).",
        "",
    ]
    rls = ["-- Phase 0 RLS: authenticated users only (tightened to roles in Phase 4).", ""]
    rls.append(
        """-- Profiles: one row per auth user, holds the role used by later RLS phases.
create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text unique,
  role text not null default 'Requester',
  craft text default '',
  groups text[] default '{}',
  created_at timestamptz default now()
);
alter table public.profiles enable row level security;
drop policy if exists "profiles self read" on public.profiles;
create policy "profiles self read"  on public.profiles for select to authenticated using (true);
drop policy if exists "profiles self write" on public.profiles;
create policy "profiles self write" on public.profiles for all to authenticated using (auth.uid() = id) with check (auth.uid() = id);
"""
    )
    tables = []
    for path in sorted(glob.glob(os.path.join(ENTITY_DIR, "*.kt"))):
        parsed = parse_entity(path)
        if not parsed:
            continue
        table, cols = parsed
        if not any(c[3] for c in cols):
            print("WARN no id pk:", table, file=sys.stderr)
        tables.append(table)
        coldefs = []
        for name, pg, nullable, is_pk in cols:
            if is_pk:
                coldefs.append(f'  "{name}" {pg} primary key')
            else:
                coldefs.append(f'  "{name}" {pg}' + ("" if nullable else " not null"))
        schema.append(f"create table if not exists public.{table} (\n" + ",\n".join(coldefs) + "\n);")
        schema.append("")
        rls.append(f"alter table public.{table} enable row level security;")
        rls.append(f'drop policy if exists "{table} auth all" on public.{table};')
        rls.append(
            f'create policy "{table} auth all" on public.{table} for all to authenticated using (true) with check (true);'
        )
    schema.append(f"-- {len(tables)} tables generated.")
    os.makedirs(OUT_DIR, exist_ok=True)
    open(os.path.join(OUT_DIR, "schema.sql"), "w").write("\n".join(schema) + "\n")
    open(os.path.join(OUT_DIR, "rls_phase0.sql"), "w").write("\n".join(rls) + "\n")
    print(f"Generated {len(tables)} tables -> supabase/schema.sql, supabase/rls_phase0.sql")


if __name__ == "__main__":
    main()
