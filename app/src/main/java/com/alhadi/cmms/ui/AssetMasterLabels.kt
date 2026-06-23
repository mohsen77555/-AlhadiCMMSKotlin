package com.alhadi.cmms.ui

internal val assetCategoryOptions = listOf(
    "Machine",
    "Vehicle",
    "Tool",
    "ProductionResource",
    "CustomerAsset",
    "Facility",
    "Assembly",
    "Other"
)

internal fun assetCategoryLabel(value: String): String = when (value) {
    "Machine" -> "آلة / معدة"
    "Vehicle" -> "مركبة"
    "Tool" -> "أداة"
    "ProductionResource" -> "مورد إنتاج"
    "CustomerAsset" -> "أصل عميل"
    "Facility" -> "مرفق"
    "Assembly" -> "تجميعة"
    "Other" -> "أخرى"
    else -> value.ifBlank { "غير محدد" }
}

internal val assetPartnerRoleOptions = listOf(
    "",
    "Manufacturer",
    "Vendor",
    "ServiceProvider",
    "Operator",
    "Owner",
    "Responsible"
)

internal fun assetPartnerRoleLabel(value: String): String = when (value) {
    "Manufacturer" -> "الشركة المصنّعة"
    "Vendor" -> "المورّد"
    "ServiceProvider" -> "مقدم الخدمة"
    "Operator" -> "المشغّل"
    "Owner" -> "المالك"
    "Responsible" -> "المسؤول"
    else -> "بدون"
}
