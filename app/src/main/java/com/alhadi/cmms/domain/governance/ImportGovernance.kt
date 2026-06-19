package com.alhadi.cmms.domain.governance

private val safeCodeChars = Regex("[^A-Z0-9._-]")

data class ImportIssueDraft(
    val sheetName: String,
    val rowNumber: Int,
    val fieldName: String = "",
    val severity: String,
    val code: String,
    val message: String,
    val rawValue: String = ""
)

data class ImportValidationReport(
    val modelName: String,
    val assetCode: String,
    val totalRows: Int,
    val issues: List<ImportIssueDraft>
) {
    val errorCount: Int get() = issues.count { it.severity == "Error" }
    val warningCount: Int get() = issues.count { it.severity == "Warning" }
    val isValid: Boolean get() = errorCount == 0
}

object ImportGovernance {
    fun validateMaintenanceKit(sheets: Map<String, List<List<String>>>): ImportValidationReport {
        val issues = mutableListOf<ImportIssueDraft>()
        if (sheets.isEmpty()) {
            issues += ImportIssueDraft("Workbook", 0, severity = "Error", code = "EMPTY_WORKBOOK", message = "Workbook contains no sheets.")
            return ImportValidationReport("", "", 0, issues)
        }

        fun sheet(key: String): Pair<String, List<List<String>>> =
            sheets.entries.firstOrNull { it.key.contains(key, ignoreCase = true) }
                ?.let { it.key to it.value } ?: (key to emptyList())

        val (techName, tech) = sheet("البيانات الفنية")
        if (tech.size < 2) {
            issues += ImportIssueDraft(techName, 0, severity = "Error", code = "TECH_SHEET_MISSING", message = "Technical-data sheet is missing or empty.")
        }
        val techRows = tech.drop(1).filter { it.isNotEmpty() && it[0].isNotBlank() }
        val model = techRows.firstOrNull {
            it.firstOrNull()?.contains("موديل", ignoreCase = true) == true ||
                it.firstOrNull()?.contains("Model", ignoreCase = true) == true
        }?.getOrNull(1)?.trim().orEmpty()
        if (model.isBlank()) {
            issues += ImportIssueDraft(techName, 0, "Model", "Error", "MODEL_REQUIRED", "Asset model is required.")
        }
        val code = model.uppercase().replace(Regex("\\s+"), "-").replace(safeCodeChars, "").take(60)
        if (code.isBlank()) {
            issues += ImportIssueDraft(techName, 0, "Model", "Error", "CODE_INVALID", "Unable to derive a safe asset code from model.", model)
        }

        sheets.forEach { (sheetName, rows) ->
            rows.forEachIndexed { index, row ->
                row.forEachIndexed { columnIndex, value ->
                    val trimmed = value.trim()
                    if (trimmed.startsWith("=") || trimmed.startsWith("+") || trimmed.startsWith("@")) {
                        issues += ImportIssueDraft(
                            sheetName,
                            index + 1,
                            "Column ${columnIndex + 1}",
                            "Error",
                            "FORMULA_NOT_ALLOWED",
                            "Formula-like input is not allowed in governed imports.",
                            value.take(120)
                        )
                    }
                }
            }
        }

        val (partsName, partsSheet) = sheet("قطع الغيار")
        if (partsSheet.isEmpty()) {
            issues += ImportIssueDraft(partsName, 0, severity = "Warning", code = "PARTS_SHEET_MISSING", message = "Spare-parts sheet is missing.")
        } else {
            val seen = mutableSetOf<String>()
            partsSheet.drop(1).forEachIndexed { index, row ->
                val partNumber = row.getOrNull(1)?.trim().orEmpty()
                if (partNumber.isBlank() || partNumber == "—" || partNumber.equals("Code", true)) return@forEachIndexed
                val normalized = partNumber.uppercase()
                if (!seen.add(normalized)) {
                    issues += ImportIssueDraft(partsName, index + 2, "Part number", "Error", "DUPLICATE_PART", "Duplicate part number in workbook.", partNumber)
                }
                if (normalized.replace(safeCodeChars, "") != normalized) {
                    issues += ImportIssueDraft(partsName, index + 2, "Part number", "Error", "PART_CODE_INVALID", "Part number contains unsupported characters.", partNumber)
                }
                if (row.getOrNull(2).isNullOrBlank()) {
                    issues += ImportIssueDraft(partsName, index + 2, "Name", "Warning", "PART_NAME_EMPTY", "Spare part name is empty.", partNumber)
                }
            }
        }

        listOf("خطة الصيانة", "إجراءات الصيانة").forEach { key ->
            val (name, rows) = sheet(key)
            if (rows.size < 2) issues += ImportIssueDraft(name, 0, severity = "Warning", code = "OPTIONAL_SHEET_EMPTY", message = "$key sheet is missing or empty.")
        }

        return ImportValidationReport(model, code, sheets.values.sumOf { it.size }, issues)
    }
}
