# Al-Hadi CMMS — Backend (Firebase Cloud Functions)

TypeScript Cloud Functions backend for the Al-Hadi CMMS/EAM app. It complements the offline-first
Firestore sync with the four capabilities that can only run server-side.

## Capabilities

| Area | Functions | Type |
|------|-----------|------|
| **User & role management** | `adminCreateUser`, `adminSetRole`, `adminSetPassword`, `adminDeleteUser`, `setupAdminClaim` | Callable (admin-only) |
| **Role security (custom claims)** | `onAuthUserCreate`, `onUserProfileWritten` | Auth + Firestore triggers |
| **Server automation** | `generatePreventiveWorkOrders`, `autoReorderLowStock`, `escalateOverdueWorkOrders` | Scheduled (Pub/Sub) |
| **Push notifications** | `onWorkOrderWritten`, `onMaintenanceRequestCreated` | Firestore triggers (FCM) |
| **Reporting & export** | `maintenanceKpis`, `exportWorkOrdersCsv` | Callable |

## How it fits the app

- **Auth**: users sign in with a username mapped to a synthetic email `username@alhadi.local`
  (handled by the client `FirebaseAuthGateway`). The backend mirrors that exact mapping and password
  derivation in `common.ts`, so accounts created on the server and on the client are interchangeable.
- **Roles**: stored as a Firebase custom claim `role`. The claim is set when the Auth account is first
  created (first login) from the `users/{username}` profile, and refreshed whenever the profile
  changes. `firestore.rules` authorizes by that claim.
- **IDs**: documents the backend creates (work orders from PM, draft POs, lines) use `newId()` — a
  high, collision-free id that round-trips into the app's Room database via the existing pull-sync.
- **Notifications**: the app registers each device's FCM token in `device_tokens/{token}`
  (`FcmGateway` on login); the triggers fan messages out by username or role.

## One-time setup

1. **Install the Firebase CLI** and log in: `npm i -g firebase-tools && firebase login`.
2. **Enable Email/Password** sign-in: Firebase Console → Authentication → Sign-in method.
3. **Install deps & build**: `cd functions && npm install && npm run build`.
4. **Set the bootstrap secret** (for the first admin claim):
   `firebase functions:secrets:set ADMIN_BOOTSTRAP_SECRET` (or set it as an env var for the function).
5. **Deploy**: from the repo root, `firebase deploy --only functions,firestore:rules,firestore:indexes`.
6. **Bootstrap the first admin**: have the admin log in once in the app (creates their Auth account),
   then call `setupAdminClaim({ username, secret })` once. They must sign out/in (or the app must
   force-refresh the ID token) to pick up the new claim. After that, manage everyone else with
   `adminSetRole` / `adminCreateUser`.
7. **Turn OFF Test mode** in Firestore once the rules are deployed and login works.

## Local development

```bash
cd functions
npm install
npm run build          # type-check / compile to lib/
firebase emulators:start --only functions,firestore,auth
```

## Notes / limitations

- Fine-grained role enforcement lives in both the rules (this backend) and the app UI.
- `setupAdminClaim` is a deliberate, secret-gated bootstrap; rotate the secret after first use.
- Scheduled jobs mirror the in-app logic and are idempotent (guarded by `lastGeneratedDueAt`,
  dated PO numbers, and `escalatedAt`) so they can safely run daily.
