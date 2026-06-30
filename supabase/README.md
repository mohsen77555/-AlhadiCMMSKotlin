# Supabase backend (migration target)

This folder holds the Supabase side of the Firebase→Supabase migration. See
`../docs/SUPABASE_MIGRATION.md` for the full plan.

## Project
- **URL:** `https://hhhpafpjaphtzbnedhlu.supabase.co`
- **Anon (publishable) key:** lives in the Android app config (public, safe).
- **Service-role key:** secret — never commit it; store it as a GitHub repo secret
  `SUPABASE_SERVICE_ROLE` for admin/CI scripts only.

## Files
- `schema.sql` — Postgres tables for all 36 Room entities (ids are client-supplied bigint PKs that
  match the app's Room ids). Generated from the entities.
- `rls_phase0.sql` — creates the `profiles` table and enables **authenticated-only** RLS on every
  table (a safe starting point; role-based policies come in Phase 4).
- `gen_schema.py` — regenerates `schema.sql` + `rls_phase0.sql` from the Kotlin entities. Run from the
  repo root: `python3 supabase/gen_schema.py`. Re-run whenever an entity changes.

## Phase 0 — apply the schema (do this in the Supabase dashboard)
1. Open your project → **SQL Editor** → New query.
2. Paste the contents of `schema.sql` → **Run**.
3. New query → paste `rls_phase0.sql` → **Run**.
4. Verify under **Table Editor** that the tables exist with RLS enabled.

> Phase 0 changes nothing in the app yet — it only provisions the database. The app keeps running on
> Firebase until we cut over in later phases.

## Notes
- Columns are NOT NULL where the Kotlin field is non-null; nullable Kotlin fields (`Type?`) map to
  nullable columns. The app always sends full rows on upsert, so this is safe.
- Computed Kotlin properties (`val x: T get() = ...`) are intentionally not columns.
