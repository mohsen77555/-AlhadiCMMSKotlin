package com.alhadi.cmms.ui

import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SparePartEntity

internal val bomCategoryOptions = listOf("Asset", "Location", "Material")
internal val bomUsageOptions = listOf("Maintenance", "Engineering", "Production", "Costing", "Universal")
internal val bomAssignmentOptions = listOf("Direct", "Indirect")
internal val bomHeaderStatusOptions = listOf("Active", "Inactive", "Blocked")
internal val bomItemCategoryOptions = listOf("Stock", "NonStock", "Assembly", "Text")
internal val bomItemStatusOptions = listOf("Active", "Inactive", "Blocked")

internal fun bomCategoryLabel(value: String): String = when (value) {
    "Asset" -> "أصل"
    "Location" -> "موقع فني"
    "Material" -> "نوع إنشاء مشترك"
    else -> value
}

internal fun bomUsageLabel(value: String): String = when (value) {
    "Maintenance" -> "صيانة"
    "Engineering" -> "هندسة وتصميم"
    "Production" -> "إنتاج"
    "Costing" -> "تكاليف"
    "Universal" -> "استخدام عام"
    else -> value
}

internal fun bomAssignmentLabel(value: String): String = when (value) {
    "Indirect" -> "مشترك حسب نوع الإنشاء"
    else -> "تعيين مباشر"
}

internal fun bomStatusLabel(value: String): String = when (value) {
    "Active" -> "نشطة"
    "Inactive" -> "غير نشطة"
    "Blocked" -> "محظورة"
    else -> value
}

internal fun bomItemCategoryLabel(value: String): String = when (value) {
    "Stock" -> "قطعة مخزنية"
    "NonStock" -> "قطعة غير مخزنية"
    "Assembly" -> "تجميعة صيانة"
    "Text" -> "عنصر هيكلي / نصي"
    else -> value
}

internal fun resolveAssetBomHeaders(
    asset: AssetEntity,
    headers: List<AssetBomHeaderEntity>
): List<AssetBomHeaderEntity> = headers
    .filter { header ->
        when (header.assignmentType) {
            "Indirect" -> asset.constructionType.isNotBlank() &&
                header.constructionType.equals(asset.constructionType, ignoreCase = true)
            else -> header.assetId == asset.id
        }
    }
    .sortedWith(compareBy({ it.code }, { it.alternative }))

internal fun bomHeaderUsableInOrder(header: AssetBomHeaderEntity, today: String): Boolean =
    header.isActiveOn(today) && (header.usage == "Maintenance" || header.usage == "Universal")

internal fun bomItemUsableInOrder(item: AssetBomItemEntity, today: String): Boolean =
    item.isMaterialItem() && item.partId != 0L && item.useInOrders && item.isActiveOn(today)

internal fun bomItemObjectLabel(
    item: AssetBomItemEntity,
    partsById: Map<Long, SparePartEntity>,
    assetsById: Map<Long, AssetEntity>
): String = when (item.itemCategory) {
    "Assembly" -> item.assemblyAssetId?.let { id ->
        assetsById[id]?.let { "${it.code} • ${it.name}" }
    } ?: "تجميعة غير محددة"
    "Text" -> item.notes.ifBlank { "عنصر هيكلي" }
    else -> partsById[item.partId]?.let { "${it.partNumber} • ${it.name}" } ?: "قطعة #${item.partId}"
}

internal fun bomItemDepth(item: AssetBomItemEntity, allItems: List<AssetBomItemEntity>): Int {
    val byId = allItems.associateBy { it.id }
    val visited = mutableSetOf<Long>()
    var depth = 0
    var parentId = item.parentItemId
    while (parentId != null && visited.add(parentId) && depth < 8) {
        depth += 1
        parentId = byId[parentId]?.parentItemId
    }
    return depth
}
