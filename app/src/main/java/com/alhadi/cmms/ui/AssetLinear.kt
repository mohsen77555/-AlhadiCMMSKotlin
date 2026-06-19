package com.alhadi.cmms.ui

import com.alhadi.cmms.data.entity.AssetEntity
import java.util.Locale

internal val linearUnitOptions = listOf("m", "km", "ft", "mi")
internal val linearOffsetUnitOptions = listOf("mm", "cm", "m", "ft")
internal val linearDirectionOptions = listOf("Both", "Forward", "Reverse", "Clockwise", "CounterClockwise")
internal val networkObjectTypeOptions = listOf("", "Route", "Segment", "Node", "Pipeline", "Road", "Track", "Cable", "Circuit", "Other")
internal val networkRelationOptions = listOf("", "BelongsTo", "ConnectsTo", "Feeds", "Crosses", "Parallel", "Contains", "Other")

internal fun linearUnitLabel(value: String): String = when (value) {
    "m" -> "متر"
    "km" -> "كيلومتر"
    "ft" -> "قدم"
    "mi" -> "ميل"
    "mm" -> "ملم"
    "cm" -> "سم"
    else -> value
}

internal fun linearDirectionLabel(value: String): String = when (value) {
    "Forward" -> "اتجاه أمامي"
    "Reverse" -> "اتجاه عكسي"
    "Clockwise" -> "مع عقارب الساعة"
    "CounterClockwise" -> "عكس عقارب الساعة"
    else -> "الاتجاهان"
}

internal fun networkObjectTypeLabel(value: String): String = when (value) {
    "Route" -> "مسار"
    "Segment" -> "مقطع"
    "Node" -> "عقدة"
    "Pipeline" -> "خط أنابيب"
    "Road" -> "طريق"
    "Track" -> "سكة"
    "Cable" -> "كابل"
    "Circuit" -> "دائرة"
    "Other" -> "أخرى"
    else -> "بدون"
}

internal fun networkRelationLabel(value: String): String = when (value) {
    "BelongsTo" -> "يتبع"
    "ConnectsTo" -> "يتصل بـ"
    "Feeds" -> "يغذّي"
    "Crosses" -> "يتقاطع مع"
    "Parallel" -> "موازٍ لـ"
    "Contains" -> "يحتوي"
    "Other" -> "أخرى"
    else -> "بدون"
}

internal fun formatLinearNumber(value: Double): String {
    if (!value.isFinite()) return "—"
    return String.format(Locale.US, "%.3f", value).trimEnd('0').trimEnd('.')
}

internal fun linearRangeLabel(asset: AssetEntity): String =
    if (!asset.isLinearAsset || !asset.hasValidLinearRange()) {
        "غير محدد"
    } else {
        "${formatLinearNumber(asset.linearStartPoint)} – ${formatLinearNumber(asset.linearEndPoint)} ${asset.linearUnit}"
    }

internal fun linearMaintenancePositionLabel(
    asset: AssetEntity,
    startPoint: Double?,
    endPoint: Double?,
    marker: String,
    horizontalOffset: Double?,
    verticalOffset: Double?
): String {
    val parts = mutableListOf<String>()
    if (startPoint != null && endPoint != null) {
        parts += "${formatLinearNumber(startPoint)} – ${formatLinearNumber(endPoint)} ${asset.linearUnit}"
    }
    if (marker.isNotBlank()) parts += "العلامة: $marker"
    val offsets = buildList {
        if (horizontalOffset != null) add("أفقي ${formatLinearNumber(horizontalOffset)} ${asset.linearOffsetUnit}")
        if (verticalOffset != null) add("رأسي ${formatLinearNumber(verticalOffset)} ${asset.linearOffsetUnit}")
    }
    if (offsets.isNotEmpty()) parts += offsets.joinToString("، ")
    return parts.joinToString(" • ").ifBlank { "غير محدد" }
}

internal fun optionalLinearRangeValid(asset: AssetEntity, startText: String, endText: String): Boolean {
    if (startText.isBlank() && endText.isBlank()) return true
    val start = startText.toDoubleOrNull() ?: return false
    val end = endText.toDoubleOrNull() ?: return false
    return asset.containsLinearRange(start, end)
}

internal fun optionalLinearNumberValid(value: String): Boolean = value.isBlank() || value.toDoubleOrNull() != null
