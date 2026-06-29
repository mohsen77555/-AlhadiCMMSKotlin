/**
 * Al-Hadi CMMS — Cloud Functions backend entry point.
 *
 * Capability areas (all initialized via ./common, which boots the Admin SDK):
 *  - User & role management ............ ./users   (callable, admin-only)
 *  - Role custom-claims sync ........... ./claims  (auth + Firestore triggers)
 *  - Server automation (scheduling) .... ./scheduling (Pub/Sub scheduled jobs)
 *  - Push notifications ................ ./notifications (Firestore triggers)
 *  - Reporting & export ................ ./reports (callable)
 */
export {
  adminCreateUser,
  adminSetRole,
  adminSetPassword,
  adminDeleteUser,
  setupAdminClaim,
} from "./users";

export { onAuthUserCreate, onUserProfileWritten } from "./claims";

export { generatePreventiveWorkOrders, autoReorderLowStock, escalateOverdueWorkOrders } from "./scheduling";

export { onWorkOrderWritten, onMaintenanceRequestCreated } from "./notifications";

export { maintenanceKpis, exportWorkOrdersCsv } from "./reports";
