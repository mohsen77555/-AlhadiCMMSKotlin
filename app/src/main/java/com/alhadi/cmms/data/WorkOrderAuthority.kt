package com.alhadi.cmms.data

import com.alhadi.cmms.data.entity.UserEntity

/**
 * Per-action work-order authorization (WO-AUTH-001..010).
 *
 * - Admin / Supervisor ("manager") may create, edit, release, assign, close,
 *   cancel, approve and view costs.
 * - Reopening a closed order and viewing the audit log are Admin-only.
 * - Any inactive/anonymous user is denied.
 */
object WorkOrderAuthority {
    private fun UserEntity?.isManager(): Boolean = this?.isActive == true && this.canManage
    private fun UserEntity?.isAdminUser(): Boolean = this?.isActive == true && this.isAdmin

    fun canCreate(user: UserEntity?): Boolean = user.isManager()       // WO-AUTH-001
    fun canEdit(user: UserEntity?): Boolean = user.isManager()         // WO-AUTH-002
    fun canRelease(user: UserEntity?): Boolean = user.isManager()      // WO-AUTH-003
    fun canAssign(user: UserEntity?): Boolean = user.isManager()       // WO-AUTH-004
    fun canClose(user: UserEntity?): Boolean = user.isManager()        // WO-AUTH-005
    fun canReopen(user: UserEntity?): Boolean = user.isAdminUser()     // WO-AUTH-006
    fun canCancel(user: UserEntity?): Boolean = user.isManager()       // WO-AUTH-007
    fun canViewCosts(user: UserEntity?): Boolean = user.isManager()    // WO-AUTH-008
    fun canViewAudit(user: UserEntity?): Boolean = user.isAdminUser()  // WO-AUTH-009
    fun canApprove(user: UserEntity?): Boolean = user.isManager()      // WO-AUTH-010
}
