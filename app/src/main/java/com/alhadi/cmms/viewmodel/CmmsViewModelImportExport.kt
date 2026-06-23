package com.alhadi.cmms.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.alhadi.cmms.data.* // repository CRUD methods are extension functions in this package
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.PdfExporter
import com.alhadi.cmms.util.XlsxReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Imports a maintenance-kit workbook the user picked (content URI). */
fun CmmsViewModel.importExcel(context: Context, uri: Uri) {
    viewModelScope.launch {
        runCatching {
            withContext(Dispatchers.IO) {
                val sheets = context.contentResolver.openInputStream(uri)?.use { XlsxReader.read(it) }
                    ?: throw IllegalStateException("تعذّر فتح الملف")
                repository.importMachineKit(sheets, actor())
            }
        }.onSuccess { _message.value = it }
            .onFailure { _message.value = "تعذّر الاستيراد: ${it.message ?: "ملف غير صالح"}" }
    }
}

/** Imports the maintenance-kit workbook bundled with the app (assets/). */
fun CmmsViewModel.importBundledKit(context: Context, assetFile: String = "FVV_maintenance_kit.xlsx") {
    viewModelScope.launch {
        runCatching {
            withContext(Dispatchers.IO) {
                val sheets = context.assets.open(assetFile).use { XlsxReader.read(it) }
                repository.importMachineKit(sheets, actor())
            }
        }.onSuccess { _message.value = it }
            .onFailure { _message.value = "تعذّر استيراد القالب المرفق: ${it.message ?: "غير معروف"}" }
    }
}

/** Writes a full JSON backup of the database to the user-chosen file. */
fun CmmsViewModel.exportBackup(context: Context, uri: Uri) {
    viewModelScope.launch {
        runCatching {
            withContext(Dispatchers.IO) {
                val content = repository.exportBackup()
                context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray()) }
                    ?: throw IllegalStateException("تعذّر إنشاء الملف")
            }
        }.onSuccess { _message.value = "تم حفظ النسخة الاحتياطية بنجاح" }
            .onFailure { _message.value = "تعذّر التصدير: ${it.message ?: "غير معروف"}" }
    }
}

/** Replaces all data with the contents of a chosen JSON backup file. */
fun CmmsViewModel.importBackup(context: Context, uri: Uri) {
    viewModelScope.launch {
        runCatching {
            withContext(Dispatchers.IO) {
                val content = context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
                    ?: throw IllegalStateException("تعذّر فتح الملف")
                repository.importBackup(content)
            }
        }.onSuccess { _message.value = "تمت الاستعادة بنجاح (${it.totalRecords} سجل)" }
            .onFailure { _message.value = "تعذّر الاستعادة: ${it.message ?: "ملف غير صالح"}" }
    }
}

/** Renders the given work order to a printable PDF at the chosen file location. */
fun CmmsViewModel.exportWorkOrderPdf(context: Context, uri: Uri, order: WorkOrderEntity) {
    viewModelScope.launch {
        runCatching {
            withContext(Dispatchers.IO) {
                val asset = repository.assets.first().firstOrNull { it.id == order.assetId }
                val operations = repository.workOrderOperations.first().filter { it.orderId == order.id }
                val materials = repository.transactionsForOrder(order.id)
                val partsById = repository.spareParts.first().associateBy { it.id }
                val permits = repository.workPermits.first().filter { it.orderId == order.id }
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    PdfExporter.writeWorkOrder(
                        out = out,
                        order = order,
                        asset = asset,
                        operations = operations,
                        materials = materials,
                        partsById = partsById,
                        permits = permits,
                        generatedAt = DateStrings.now()
                    )
                } ?: throw IllegalStateException("تعذّر إنشاء الملف")
            }
        }.onSuccess { _message.value = "تم تصدير أمر العمل PDF" }
            .onFailure { _message.value = "تعذّر تصدير PDF: ${it.message ?: "غير معروف"}" }
    }
}
