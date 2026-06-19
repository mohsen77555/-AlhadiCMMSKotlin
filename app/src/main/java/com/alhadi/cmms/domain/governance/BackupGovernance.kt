package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.BackupBundle

object BackupGovernance {
    const val CURRENT_DB_VERSION = 26

    fun validateForRestore(bundle: BackupBundle): BackupValidationResult {
        val errors = mutableListOf<String>()

        if (bundle.formatVersion != BackupBundle.CURRENT_FORMAT_VERSION) {
            errors += "Unsupported backup format version: ${bundle.formatVersion}."
        }
        if (bundle.appDbVersion <= 0) {
            errors += "Backup database version is missing."
        }
        if (bundle.appDbVersion > CURRENT_DB_VERSION) {
            errors += "Backup database version is newer than this app: ${bundle.appDbVersion}."
        }
        if (bundle.totalRecords == 0) {
            errors += "Backup contains no records."
        }
        if (bundle.users.isEmpty()) {
            errors += "Backup must contain at least one user record."
        }

        return BackupValidationResult(isValid = errors.isEmpty(), errors = errors)
    }

    fun requireValidForRestore(bundle: BackupBundle) {
        val result = validateForRestore(bundle)
        if (!result.isValid) {
            throw IllegalStateException(result.errors.joinToString(separator = "\n"))
        }
    }
}

data class BackupValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)
