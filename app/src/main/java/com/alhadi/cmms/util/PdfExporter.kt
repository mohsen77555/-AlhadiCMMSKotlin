package com.alhadi.cmms.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.OutputStream

/**
 * Renders a printable, RTL Arabic A4 PDF of a work order using the platform [PdfDocument].
 * Text is laid out with [StaticLayout] so Arabic shaping, bidi and wrapping are correct.
 */
object PdfExporter {
    private const val PAGE_W = 595 // A4 @ 72dpi
    private const val PAGE_H = 842
    private const val MARGIN = 36f
    private val CONTENT_W = (PAGE_W - 2 * MARGIN).toInt()

    private fun paint(size: Float, bold: Boolean = false, color: Int = Color.BLACK) = TextPaint().apply {
        isAntiAlias = true
        textSize = size
        this.color = color
        typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
    }

    fun writeWorkOrder(
        out: OutputStream,
        order: WorkOrderEntity,
        asset: AssetEntity?,
        operations: List<WorkOrderOperationEntity>,
        materials: List<InventoryTransactionEntity>,
        partsById: Map<Long, SparePartEntity>,
        permits: List<WorkPermitEntity>,
        generatedAt: String
    ) {
        val doc = PdfDocument()
        val pager = Pager(doc)

        pager.paragraph("أمر عمل #${order.id}", paint(20f, bold = true))
        pager.paragraph(order.title, paint(14f, bold = true))
        pager.rule()

        pager.heading("بيانات الأمر")
        pager.kv("الأصل", asset?.let { "${it.code} — ${it.name}" } ?: "#${order.assetId}")
        pager.kv("الموقع", asset?.location ?: "—")
        pager.kv("الحالة", arabicStatus(order.status))
        pager.kv("الأولوية", order.priority)
        pager.kv("المسؤول", order.assignedTo.ifBlank { "غير محدد" })
        pager.kv("تاريخ الإنشاء", order.createdAt)
        pager.kv("تاريخ الاستحقاق", order.dueAt)
        pager.kv("الاعتماد", arabicApproval(order.approvalStatus))

        if (order.description.isNotBlank()) {
            pager.heading("الوصف")
            pager.body(order.description)
        }

        if (operations.isNotEmpty()) {
            pager.heading("العمليات (${operations.size})")
            operations.forEachIndexed { i, op ->
                val done = if (op.status == "Done" || op.status == "Completed") "✓" else "□"
                pager.body("$done ${i + 1}. ${op.description}")
            }
        }

        if (materials.isNotEmpty()) {
            val total = materials.sumOf { (partsById[it.partId]?.lastPrice ?: 0.0) * it.quantity }
            pager.heading("المواد المستهلكة (${materials.size})")
            materials.forEach { tx ->
                val p = partsById[tx.partId]
                pager.body("× ${tx.quantity}   ${p?.partNumber ?: "#${tx.partId}"} — ${p?.name ?: ""}")
            }
            pager.kv("إجمالي قيمة المواد", money(total))
        }

        if (permits.isNotEmpty()) {
            pager.heading("تصاريح العمل (${permits.size})")
            permits.forEach { permit ->
                pager.body("• ${permit.type} — ${arabicApproval(permit.status)} — صالح حتى ${permit.validUntil}")
            }
        }

        pager.heading("التكاليف")
        pager.kv("العمالة", "${order.laborHours} ساعة × ${money(order.laborRate)}")
        pager.kv("قطع الغيار", money(order.partsCost))
        val labor = order.laborHours * order.laborRate
        pager.kv("الإجمالي", money(labor + order.partsCost))

        pager.rule()
        pager.heading("التوقيعات")
        pager.body("المنفّذ: ______________________        المعتمِد: ______________________")
        pager.gap(16f)
        pager.footer("الهادي — نظام إدارة الصيانة • صُدِّر في $generatedAt")

        pager.finish()
        doc.writeTo(out)
        doc.close()
    }

    /** A titled block of key/value rows for the KPI report. */
    data class ReportSection(val title: String, val rows: List<Pair<String, String>>)

    /** Renders a printable management KPI report (RTL). */
    fun writeReport(out: OutputStream, title: String, generatedAt: String, sections: List<ReportSection>) {
        val doc = PdfDocument()
        val pager = Pager(doc)
        pager.paragraph(title, paint(20f, bold = true))
        pager.footer("الهادي — نظام إدارة الصيانة • صُدِّر في $generatedAt")
        pager.rule()
        sections.forEach { section ->
            pager.heading(section.title)
            section.rows.forEach { (key, value) -> pager.kv(key, value) }
        }
        pager.finish()
        doc.writeTo(out)
        doc.close()
    }

    /** Renders a printable QR label for an asset (big QR + code + name) to stick on the machine. */
    fun writeAssetLabel(out: OutputStream, asset: AssetEntity) {
        val doc = PdfDocument()
        val page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create())
        val canvas = page.canvas

        val center = PAGE_W / 2f
        val codePaint = paint(40f, bold = true).apply { textAlign = Paint.Align.CENTER }
        val namePaint = paint(20f).apply { textAlign = Paint.Align.CENTER }
        val hintPaint = paint(13f, color = Color.GRAY).apply { textAlign = Paint.Align.CENTER }

        var y = 140f
        canvas.drawText(asset.code, center, y, codePaint)
        y += 36f
        canvas.drawText(asset.name, center, y, namePaint)

        val qrSize = 360
        val qr = qrBitmap("ALHADI:${asset.code}", qrSize)
        y += 40f
        canvas.drawBitmap(qr, center - qrSize / 2f, y, null)
        y += qrSize + 36f
        canvas.drawText("امسح الرمز للوصول السريع لبطاقة الأصل", center, y, hintPaint)
        y += 22f
        canvas.drawText("${asset.groupName} • ${asset.location}", center, y, hintPaint)

        doc.finishPage(page)
        doc.writeTo(out)
        doc.close()
    }

    private fun qrBitmap(content: String, size: Int): Bitmap {
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            val offset = y * size
            for (x in 0 until size) {
                pixels[offset + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            .also { it.setPixels(pixels, 0, size, 0, 0, size, size) }
    }

    private fun money(value: Double): String = "%,.2f ر.س".format(value)

    private fun arabicStatus(status: String): String = when (status) {
        "Open" -> "مفتوح"; "In Progress" -> "قيد التنفيذ"
        "Technically Completed" -> "مكتمل فنياً"; "Closed" -> "مغلق"; else -> status
    }

    private fun arabicApproval(status: String): String = when (status) {
        "Approved" -> "معتمد"; "Rejected" -> "مرفوض"; "Pending" -> "بانتظار الاعتماد"
        "Closed" -> "مغلق"; else -> status
    }

    /** Stateful helper that draws paragraphs top-to-bottom and starts a new page when needed. */
    private class Pager(private val doc: PdfDocument) {
        private var pageNumber = 1
        private var page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create())
        private var canvas: Canvas = page.canvas
        private var y = MARGIN

        private fun ensure(height: Float) {
            if (y + height > PAGE_H - MARGIN) {
                doc.finishPage(page)
                pageNumber += 1
                page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create())
                canvas = page.canvas
                y = MARGIN
            }
        }

        fun paragraph(text: String, p: TextPaint) {
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, p, CONTENT_W)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setTextDirection(TextDirectionHeuristics.FIRSTSTRONG_RTL)
                .build()
            ensure(layout.height.toFloat())
            canvas.save()
            canvas.translate(MARGIN, y)
            layout.draw(canvas)
            canvas.restore()
            y += layout.height + 4f
        }

        fun heading(text: String) {
            gap(6f)
            paragraph(text, paint(13f, bold = true, color = Color.rgb(20, 80, 130)))
        }

        fun kv(key: String, value: String) = paragraph("$key: $value", paint(11f))
        fun body(text: String) = paragraph(text, paint(11f))
        fun footer(text: String) = paragraph(text, paint(9f, color = Color.GRAY))

        fun gap(h: Float) { y += h }

        fun rule() {
            ensure(8f)
            val line = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
            canvas.drawLine(MARGIN, y, PAGE_W - MARGIN, y, line)
            y += 8f
        }

        fun finish() = doc.finishPage(page)
    }
}
