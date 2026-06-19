package com.alhadi.cmms.ui

import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetEntity

internal data class ResolvedAssetCharacteristic(
    val item: AssetCharacteristicEntity,
    val sourceAsset: AssetEntity,
    val inherited: Boolean
) {
    val resolvedClassName: String
        get() = item.className.trim().ifBlank { sourceAsset.standardClass.trim().ifBlank { "عام" } }

    val identityKey: String
        get() = "${resolvedClassName.lowercase()}::${item.name.trim().lowercase()}"
}

/**
 * Resolves the asset's own characteristics plus values inherited from its nearest ancestors.
 * A direct value on the child overrides an inherited value with the same class and name.
 */
internal fun resolveAssetCharacteristics(
    asset: AssetEntity,
    allAssets: List<AssetEntity>,
    allCharacteristics: List<AssetCharacteristicEntity>
): List<ResolvedAssetCharacteristic> {
    val direct = allCharacteristics
        .filter { it.assetId == asset.id }
        .map { ResolvedAssetCharacteristic(it, asset, inherited = false) }

    if (!asset.inheritParentCharacteristics) {
        return direct.sortedWith(characteristicOrdering())
    }

    val result = direct.toMutableList()
    val occupied = direct.mapTo(mutableSetOf()) { it.identityKey }
    val assetsById = allAssets.associateBy { it.id }
    val characteristicsByAsset = allCharacteristics.groupBy { it.assetId }
    val visited = mutableSetOf<Long>()
    var parentId = asset.parentAssetId

    while (parentId != null && visited.add(parentId)) {
        val parent = assetsById[parentId] ?: break
        characteristicsByAsset[parent.id].orEmpty().forEach { item ->
            val resolved = ResolvedAssetCharacteristic(item, parent, inherited = true)
            if (occupied.add(resolved.identityKey)) result += resolved
        }
        parentId = parent.parentAssetId
    }

    return result.sortedWith(characteristicOrdering())
}

private fun characteristicOrdering() =
    compareBy<ResolvedAssetCharacteristic>({ it.resolvedClassName.lowercase() }, { it.inherited }, { it.item.name.lowercase() })

internal fun characteristicTypeLabel(value: String): String = when (value) {
    "Number" -> "رقم"
    "Boolean" -> "نعم / لا"
    "Date" -> "تاريخ"
    "List" -> "قائمة"
    else -> "نص"
}

internal fun parseCharacteristicAllowedValues(value: String): List<String> = value
    .replace('،', ',')
    .split(',')
    .map { it.trim() }
    .filter { it.isNotBlank() }
    .distinct()

internal fun characteristicDisplayValue(item: AssetCharacteristicEntity): String {
    val base = when (item.dataType) {
        "Boolean" -> when (item.value.lowercase()) {
            "true", "1", "yes" -> "نعم"
            "false", "0", "no" -> "لا"
            else -> item.value
        }
        else -> item.value
    }
    return listOf(base, item.unit).filter { it.isNotBlank() }.joinToString(" ")
}
