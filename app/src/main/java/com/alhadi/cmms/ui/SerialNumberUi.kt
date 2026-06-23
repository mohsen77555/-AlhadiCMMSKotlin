package com.alhadi.cmms.ui

internal val serialStockTypeOptions = listOf(
    "Unrestricted",
    "Quality",
    "Blocked",
    "Transfer",
    "Transit",
    "Vendor",
    "Customer"
)

internal val serialStockCheckOptions = listOf("None", "Warning", "Block")

internal fun serialStatusLabel(value: String): String = when (value) {
    "Created" -> "مسجل"
    "InStock" -> "في المخزون"
    "Issued" -> "مصروف"
    "Installed" -> "مركب"
    "InRepair" -> "قيد الإصلاح"
    "Scrapped" -> "مستبعد"
    else -> value
}

internal fun serialStockTypeLabel(value: String): String = when (value) {
    "Unrestricted" -> "متاح للاستخدام"
    "Quality" -> "تحت الفحص"
    "Blocked" -> "محظور"
    "Transfer" -> "قيد النقل"
    "Transit" -> "في الطريق"
    "Vendor" -> "لدى المورّد"
    "Customer" -> "لدى العميل"
    "Installed" -> "مركب"
    "Issued" -> "مصروف"
    else -> value.ifBlank { "غير محدد" }
}

internal fun serialStockCheckLabel(value: String): String = when (value) {
    "None" -> "بدون فحص"
    "Warning" -> "سماح مع تحذير"
    "Block" -> "منع عند الاختلاف"
    else -> value
}

internal fun serialMovementLabel(value: String): String = when (value) {
    "Create" -> "إنشاء سجل"
    "Receive" -> "استلام"
    "Issue" -> "صرف"
    "Transfer" -> "نقل"
    "Install" -> "تركيب"
    "Dismantle" -> "فك"
    "Reconcile" -> "تسوية"
    else -> value
}

internal fun parseSerialInput(value: String): List<String> = value
    .replace('،', ',')
    .replace(';', ',')
    .lines()
    .flatMap { it.split(',') }
    .map { it.trim().uppercase() }
    .filter { it.isNotBlank() }
    .distinct()
