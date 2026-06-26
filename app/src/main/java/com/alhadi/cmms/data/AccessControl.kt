package com.alhadi.cmms.data

import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.UserEntity

/**
 * Role-based access control (chapter 11). Five governed roles, each with a tailored set of
 * screen/feature permissions, plus asset-group scoping for maintenance roles.
 */
enum class AppRole { SystemAdmin, MaintenanceManager, Technician, Procurement, Warehouse }

object Roles {
    const val SYSTEM_ADMIN = "SystemAdmin"
    const val MAINTENANCE_MANAGER = "MaintenanceManager"
    const val TECHNICIAN = "Technician"
    const val PROCUREMENT = "Procurement"
    const val WAREHOUSE = "Warehouse"

    /** Canonical role values offered in the user form. */
    val ALL = listOf(SYSTEM_ADMIN, MAINTENANCE_MANAGER, TECHNICIAN, PROCUREMENT, WAREHOUSE)

    fun label(role: String): String = when (roleOf(role)) {
        AppRole.SystemAdmin -> "مدير النظام"
        AppRole.MaintenanceManager -> "مدير الصيانة"
        AppRole.Technician -> "فني صيانة"
        AppRole.Procurement -> "مشتريات"
        AppRole.Warehouse -> "مخازن"
    }
}

/** Technician crafts/trades (USR-CRAFT-001). Blank = unspecified. */
val CRAFTS = listOf("", "Electrical", "Mechanical", "Welding", "Lathe")

fun craftLabel(craft: String): String = when (craft.lowercase()) {
    "electrical" -> "كهربائي"
    "mechanical" -> "ميكانيكي"
    "welding" -> "لحام"
    "lathe" -> "خراطة"
    else -> craft
}

/** Resolves a stored role string (including legacy values) to a canonical [AppRole]. */
fun roleOf(role: String?): AppRole = when (role?.lowercase()) {
    "systemadmin", "admin" -> AppRole.SystemAdmin
    "maintenancemanager", "supervisor", "manager" -> AppRole.MaintenanceManager
    "procurement", "purchasing" -> AppRole.Procurement
    "warehouse", "stores", "store" -> AppRole.Warehouse
    "technician", "tech" -> AppRole.Technician
    else -> AppRole.Technician // least-privilege default
}

fun roleOf(user: UserEntity?): AppRole = roleOf(user?.role)

/** The set of permissions granted by a role. */
data class Permissions(
    val role: AppRole,
    val seeHome: Boolean,
    val seeWorkOrders: Boolean,
    /** Technician sees only the work orders assigned to them ("مهامي"). */
    val onlyMyWorkOrders: Boolean,
    val manageWorkOrders: Boolean,
    val seeAssets: Boolean,
    val manageAssets: Boolean,
    /** Maintenance roles are limited to their assigned asset groups. */
    val scopedAssets: Boolean,
    val seeNotifications: Boolean,
    val createNotifications: Boolean,
    val seePreventive: Boolean,
    val seeMeters: Boolean,
    val seeInventory: Boolean,
    val manageInventory: Boolean,
    val seeProcurement: Boolean,
    val seeReports: Boolean,
    val seeWarehouses: Boolean,
    /** Master data: locations, org units, task lists, suppliers, etc. */
    val seeMasterData: Boolean,
    val seeAdmin: Boolean
)

fun permissionsFor(user: UserEntity?): Permissions = when (roleOf(user)) {
    AppRole.SystemAdmin -> Permissions(
        role = AppRole.SystemAdmin,
        seeHome = true, seeWorkOrders = true, onlyMyWorkOrders = false, manageWorkOrders = true,
        seeAssets = true, manageAssets = true, scopedAssets = false,
        seeNotifications = true, createNotifications = true, seePreventive = true, seeMeters = true,
        seeInventory = true, manageInventory = true, seeProcurement = true, seeReports = true,
        seeWarehouses = true, seeMasterData = true, seeAdmin = true
    )
    AppRole.MaintenanceManager -> Permissions(
        role = AppRole.MaintenanceManager,
        seeHome = true, seeWorkOrders = true, onlyMyWorkOrders = false, manageWorkOrders = true,
        seeAssets = true, manageAssets = false, scopedAssets = true,
        seeNotifications = true, createNotifications = true, seePreventive = true, seeMeters = true,
        seeInventory = false, manageInventory = false, seeProcurement = false, seeReports = true,
        seeWarehouses = false, seeMasterData = false, seeAdmin = false
    )
    AppRole.Technician -> Permissions(
        role = AppRole.Technician,
        seeHome = true, seeWorkOrders = true, onlyMyWorkOrders = true, manageWorkOrders = true,
        seeAssets = true, manageAssets = false, scopedAssets = true,
        seeNotifications = false, createNotifications = true, seePreventive = false, seeMeters = false,
        seeInventory = false, manageInventory = false, seeProcurement = false, seeReports = false,
        seeWarehouses = false, seeMasterData = false, seeAdmin = false
    )
    AppRole.Procurement -> Permissions(
        role = AppRole.Procurement,
        seeHome = true, seeWorkOrders = false, onlyMyWorkOrders = false, manageWorkOrders = false,
        seeAssets = false, manageAssets = false, scopedAssets = false,
        seeNotifications = false, createNotifications = false, seePreventive = false, seeMeters = false,
        seeInventory = true, manageInventory = false, seeProcurement = true, seeReports = false,
        seeWarehouses = false, seeMasterData = false, seeAdmin = false
    )
    AppRole.Warehouse -> Permissions(
        role = AppRole.Warehouse,
        seeHome = true, seeWorkOrders = false, onlyMyWorkOrders = false, manageWorkOrders = false,
        seeAssets = false, manageAssets = false, scopedAssets = false,
        seeNotifications = false, createNotifications = false, seePreventive = false, seeMeters = false,
        seeInventory = true, manageInventory = true, seeProcurement = false, seeReports = false,
        seeWarehouses = true, seeMasterData = false, seeAdmin = false
    )
}

/**
 * The asset group names a user is restricted to. Returns null when unrestricted (admin/procurement/
 * warehouse, or a maintenance user with no groups configured yet).
 */
fun visibleAssetGroups(user: UserEntity?): Set<String>? {
    if (!permissionsFor(user).scopedAssets) return null
    val groups = user?.assignedGroupSet ?: emptySet()
    return groups.ifEmpty { null }
}

/** Whether [asset] is within the user's scope. */
fun isAssetVisible(user: UserEntity?, asset: AssetEntity): Boolean {
    val groups = visibleAssetGroups(user) ?: return true
    return asset.groupName in groups
}
