package com.example.data.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.InventoryItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * أدوات تصدير المخزون ومشاركته كملفات CSV و PDF
 */
object InventoryExportHelper {

    /**
     * تصدير بيانات المخزون كملف CSV ومشاركته عبر نافذة المشاركة لنظام أندرويد
     */
    fun exportAndShareCsv(context: Context, items: List<InventoryItem>) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "workshop_inventory_$timeStamp.csv"
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val file = File(exportDir, fileName)

            FileOutputStream(file).use { outputStream ->
                // Write UTF-8 BOM so Excel opens Arabic text properly
                outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                
                val writer = outputStream.bufferedWriter(Charsets.UTF_8)
                // Header
                writer.write("\"كود الصنف (SKU)\",\"اسم العنصر\",\"التصنيف\",\"رمز التصنيف\",\"مكان التخزين\",\"الكمية\",\"الوحدة\",\"المواصفات الفنية\",\"ملاحظات\"\n")
                
                // Rows
                items.forEach { item ->
                    val sku = escapeCsv(item.sku)
                    val name = escapeCsv(item.name)
                    val catName = escapeCsv(item.categoryName)
                    val catCode = escapeCsv(item.categoryCode)
                    val location = escapeCsv(item.storageLocation)
                    val qty = item.quantity.toString()
                    val unit = escapeCsv(item.unit)
                    val specs = escapeCsv(item.technicalSpecs)
                    val notes = escapeCsv(item.notes)
                    
                    writer.write("\"$sku\",\"$name\",\"$catName\",\"$catCode\",\"$location\",$qty,\"$unit\",\"$specs\",\"$notes\"\n")
                }
                writer.flush()
            }

            shareFile(
                context = context,
                file = file,
                mimeType = "text/csv",
                title = "مشاركة سجلات المخزون (CSV)"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "فشل تصدير ملف CSV: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * تصدير بيانات المخزون كملف PDF منظم وجاهز للطباعة والمشاركة
     */
    fun exportAndSharePdf(context: Context, items: List<InventoryItem>) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val dateLabel = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())
            val fileName = "workshop_inventory_$timeStamp.pdf"
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val file = File(exportDir, fileName)

            val pdfDocument = PdfDocument()

            // A4 Dimensions: 595 x 842 points
            val pageWidth = 595
            val pageHeight = 842
            val margin = 36f

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(30, 41, 59)
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(100, 116, 139)
                textSize = 10f
            }
            val headerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val cellTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(15, 23, 42)
                textSize = 9f
            }
            val cellBoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(15, 23, 42)
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val linePaint = Paint().apply {
                color = Color.rgb(226, 232, 240)
                strokeWidth = 0.8f
            }

            val itemsPerPage = 20
            val totalPages = if (items.isEmpty()) 1 else ((items.size - 1) / itemsPerPage) + 1

            for (pageIndex in 0 until totalPages) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                var yPos = margin + 20f

                // Header Banner on first page or all pages
                paint.color = Color.rgb(67, 56, 202) // Indigo
                canvas.drawRect(margin, yPos - 12f, pageWidth - margin, yPos + 32f, paint)

                titlePaint.color = Color.WHITE
                canvas.drawText("تقرير مخزون الورشة المعتمد - Workshop Inventory Report", margin + 14f, yPos + 8f, titlePaint)
                titlePaint.color = Color.rgb(30, 41, 59)

                subtitlePaint.color = Color.rgb(224, 231, 255)
                canvas.drawText("تاريخ التصدير: $dateLabel  |  إجمالي العناصر: ${items.size}  |  صفحة ${pageIndex + 1} من $totalPages", margin + 14f, yPos + 24f, subtitlePaint)
                subtitlePaint.color = Color.rgb(100, 116, 139)

                yPos += 52f

                // Table Column Coordinates
                val colSkuX = margin + 6f
                val colNameX = margin + 85f
                val colCatX = margin + 220f
                val colLocX = margin + 310f
                val colQtyX = margin + 415f
                val colSpecsX = margin + 465f

                // Table Header Bar
                paint.color = Color.rgb(51, 65, 85)
                canvas.drawRect(margin, yPos, pageWidth - margin, yPos + 22f, paint)

                canvas.drawText("SKU", colSkuX, yPos + 15f, headerTextPaint)
                canvas.drawText("اسم الصنف (Name)", colNameX, yPos + 15f, headerTextPaint)
                canvas.drawText("التصنيف (Category)", colCatX, yPos + 15f, headerTextPaint)
                canvas.drawText("الموقع (Location)", colLocX, yPos + 15f, headerTextPaint)
                canvas.drawText("الكمية", colQtyX, yPos + 15f, headerTextPaint)
                canvas.drawText("المواصفات (Specs)", colSpecsX, yPos + 15f, headerTextPaint)

                yPos += 22f

                // Table Rows
                val startIdx = pageIndex * itemsPerPage
                val endIdx = minOf(startIdx + itemsPerPage, items.size)

                for (i in startIdx until endIdx) {
                    val item = items[i]
                    val isEven = (i % 2 == 0)

                    // Row background
                    paint.color = if (isEven) Color.rgb(248, 250, 252) else Color.WHITE
                    canvas.drawRect(margin, yPos, pageWidth - margin, yPos + 26f, paint)

                    // Row dividing line
                    canvas.drawLine(margin, yPos + 26f, pageWidth - margin, yPos + 26f, linePaint)

                    // Cell contents (truncated safely if too long)
                    canvas.drawText(truncateText(item.sku, 14), colSkuX, yPos + 17f, cellBoldPaint)
                    canvas.drawText(truncateText(item.name, 24), colNameX, yPos + 17f, cellTextPaint)
                    canvas.drawText(truncateText(item.categoryName, 15), colCatX, yPos + 17f, cellTextPaint)
                    canvas.drawText(truncateText(item.storageLocation, 16), colLocX, yPos + 17f, cellTextPaint)
                    canvas.drawText("${item.quantity} ${item.unit}", colQtyX, yPos + 17f, cellBoldPaint)
                    canvas.drawText(truncateText(item.technicalSpecs, 16), colSpecsX, yPos + 17f, cellTextPaint)

                    yPos += 26f
                }

                // Footer
                val footerY = pageHeight - margin
                canvas.drawLine(margin, footerY - 14f, pageWidth - margin, footerY - 14f, linePaint)
                canvas.drawText("تم إنشاء هذا التقرير تلقائياً عبر نظام تصنيف ومخزون الورشة الذكي", margin, footerY, subtitlePaint)
                canvas.drawText("Page ${pageIndex + 1}/$totalPages", pageWidth - margin - 50f, footerY, subtitlePaint)

                pdfDocument.finishPage(page)
            }

            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            shareFile(
                context = context,
                file = file,
                mimeType = "application/pdf",
                title = "طباعة ومشاركة تقرير المخزون (PDF)"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "فشل إنشاء ملف PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.take(maxLength - 1) + "…"
        } else {
            text
        }
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"").replace("\n", " ")
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
