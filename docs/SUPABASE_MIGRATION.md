# Migration Plan: Firebase → Supabase

Goal: move the Al-Hadi CMMS cloud layer from Firebase (Firestore + Auth + Cloud Functions) to
**Supabase** (PostgreSQL + Supabase Auth + Realtime + Edge Functions + pg_cron), keeping the app
**offline-first** with Room as the local source of truth. Free tier, no credit card.

This is a large, staged migration. Each phase is independently shippable and CI-verifiable.

---

## 1. Why / trade-offs

| | Firebase (today) | Supabase (target) |
|---|---|---|
| DB | Firestore (NoSQL) | PostgreSQL (SQL) |
| Offline writes | queued automatically by the SDK | **we must build an outbox** (see risks) |
| Realtime pull | snapshot listeners | Realtime `postgres_changes` |
| Auth | Firebase Auth (synthetic email) | Supabase Auth (email/password) |
| Security | Firestore rules + custom claims | **RLS policies** + `profiles.role` |
| Backend | Cloud Functions (Blaze) | Edge Functions (Deno) + **pg_cron** (free) |
| Push (FCM) | triggers → FCM | Edge Function → FCM (FCM still delivers to Android) |
| Cost | Functions need Blaze (card) | Free tier, no card |

**Biggest risk:** Firestore gave us automatic offline write-queueing. Supabase does not. Room stays
the source of truth, but we must add a durable **outbox** (a pending-changes table in Room) with
retry/backoff so writes made offline reach Postgres when connectivity returns. This is the single
most important new component and is built in Phase 3.

---

## 2. Target architecture

- **Android**: Room (unchanged, local source of truth) + a new Supabase cloud layer.
- **Client lib**: `supabase-kt` (io.github.jan-tennert.supabase) — Postgrest + Realtime + Auth +
  Storage for Android. Alternative: raw PostgREST (OkHttp/Ktor) + Realtime websocket if the KMP lib
  proves unstable.
- **Postgres**: one table per Room entity (35 tables), `id bigint primary key` supplied by the client
  (matches Room ids). A `profiles` table holds role/craft/groups keyed by the Supabase auth user id.
- **Push (Room → Postgres)**: PostgREST `upsert` / `delete` through an **outbox** so it survives
  offline.
- **Pull (Postgres → Room)**: Realtime `postgres_changes` per table → write into Room DAOs (the same
  no-echo discipline we already use). Plus an initial backfill `select *` per table on startup.
- **Security**: RLS policies per table mirroring `AccessControl.kt`.
- **Automation**: `pg_cron` SQL jobs (PM generation, reorder, overdue escalation). Notifications via
  an Edge Function that calls FCM, triggered by Postgres triggers / pg_cron.

---

## 3. Tables to migrate (35)

```
assets, asset_documents, asset_characteristics, asset_bom_headers, asset_bom_items,
asset_movements, asset_installations, asset_status_history,
work_orders, work_order_operations, work_order_confirmations, work_order_photos,
work_order_materials, work_order_history, work_permits,
maintenance_notifications, preventive_maintenance, pm_checklist_items,
measuring_points, measurement_readings, capa_actions,
spare_parts, inventory_transactions, warehouses,
serial_number_profiles, serial_numbers, serial_number_movements,
suppliers, purchase_orders, purchase_order_lines,
functional_locations, org_units, task_lists, task_list_operations,
users, audit_log
```

Type mapping: `Long → bigint`, `String → text`, `Double → double precision`, `Boolean → boolean`,
nullable Kotlin fields → nullable columns. `id` is the primary key (client-supplied).

---

## 4. Phased plan

### Phase 0 — Project setup & schema  *(no app behavior change)*
- Generate the Postgres DDL for all 35 tables from the Room entities (script).
- Create a `profiles` table (`id uuid` = auth user id, `username`, `role`, `craft`, `groups text[]`).
- Apply the schema via the Supabase SQL editor (or `supabase db push`).
- Enable RLS on every table (start with an authenticated-only policy; tighten in Phase 5).
- Store the Supabase **project URL** + **anon key** in the app (BuildConfig), and the **service-role
  key** only as a GitHub secret (never in the app or chat).
- **Deliverable:** `supabase/schema.sql`, `supabase/rls_phase0.sql`.

### Phase 1 — Auth
- Add `supabase-kt` Auth. Implement `SupabaseAuthGateway` (sign-in / sign-up, email = username
  mapping kept, or switch to real emails).
- Keep local Room login as the offline path; Supabase session enables cloud access when online.
- Migrate existing users into Supabase Auth + `profiles` (one-time admin script using the service
  role).
- Custom role claim via a Supabase **Auth Hook** (access-token hook) that injects `role` from
  `profiles`, so RLS can read it from the JWT.
- **Deliverable:** `SupabaseAuthGateway.kt`, `supabase/auth_hook.sql`, user-import script.

### Phase 2 — Push sync (Room → Postgres)
- Implement `SupabasePush` (PostgREST upsert/delete per table) mirroring today's `EntityCloudSync`.
- Wire it into the repository CRUD (replace/duplicate the existing Firestore push hooks).
- **Deliverable:** `SupabasePush.kt`, repository wiring.

### Phase 3 — Offline outbox + pull sync (Postgres → Room)  *(the hard part)*
- Add a Room `outbox` table: every push enqueues an op (table, id, payload, op-type); a worker drains
  it with retry/backoff when online; clears on success. This replaces Firestore's automatic queue.
- Implement `SupabaseSyncService`: initial backfill (`select *` per table → Room) + Realtime
  `postgres_changes` subscriptions per table → Room DAOs (no echo).
- **Deliverable:** `OutboxEntity`/`OutboxDao`, `OutboxWorker`, `SupabaseSyncService.kt`.

### Phase 4 — RLS (role-based security)
- Translate `AccessControl.kt` into RLS policies per table (SELECT for authenticated; writes per role,
  including cross-domain writes like warehouse goods-receipt touching purchase orders).
- Test each of the 6 roles.
- **Deliverable:** `supabase/rls.sql`.

### Phase 5 — Backend automation
- `pg_cron` jobs: PM work-order generation, low-stock reorder, overdue escalation (SQL functions).
- Notifications: Postgres trigger / pg_cron → Edge Function → FCM HTTP v1 (Android still receives via
  FCM; device tokens stay in a `device_tokens` table).
- User/role admin: Edge Functions (or service-role scripts) for create / set-role / set-password /
  delete.
- **Deliverable:** `supabase/functions/*`, `supabase/cron.sql`.

### Phase 6 — Cutover & cleanup
- Verify feature parity for every role and every sync path.
- Remove Firebase: `google-services.json`, firebase Gradle deps, `FirebaseGateway`/`FirebaseAuthGateway`/
  `FcmGateway`/`EntityCloudSync`/`CloudSyncService`/`CloudCodec`, `functions/`, `firestore.rules`,
  `firebase.json`, `.firebaserc`, the deploy workflow.
- Update `CLAUDE.md`/docs.

---

## 5. Risks & mitigations
- **Offline writes** (no auto-queue): mitigated by the Phase-3 outbox. Highest priority to get right.
- **supabase-kt maturity on Android**: if Realtime/Postgrest prove flaky, fall back to PostgREST over
  OkHttp + a raw Realtime websocket. Validate early in Phase 2/3 with one table before scaling to 35.
- **ID strategy**: client supplies `bigint` ids (Room ids); server-created rows (pg_cron) use a high
  id range — same approach as today, so no collisions.
- **Data migration**: if there is real data in Firestore, add a one-time ETL (export Firestore →
  insert into Postgres). If it is only seed/test data, skip and reseed.
- **Two systems during transition**: Phases 1–3 can dual-run (Firebase still live) so nothing breaks
  until cutover in Phase 6.

---

## 6. What I need from you to start Phase 0
1. Supabase **Project URL** (e.g. `https://xxxx.supabase.co`) and **anon (public) key** — these are
   safe to put in the app.
2. The **service-role key** — add it as a **GitHub Actions / repo secret** named
   `SUPABASE_SERVICE_ROLE` (⚠️ never paste it in chat or commit it).
3. Confirm: keep the `username@alhadi.local` login mapping, or switch users to real email addresses?

Once provided, Phase 0 delivers the SQL schema + RLS scaffold, ready to paste into the Supabase SQL
editor.
