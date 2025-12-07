package com.basu.vaccineremainder.features.reports

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

fun generateChildReportPdf(
    context: Context,
    report: ChildReport
): File {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val titlePaint = Paint().apply {
        textSize = 18f
        isFakeBoldText = true
    }
    val normalPaint = Paint().apply {
        textSize = 12f
    }

    var y = 40f

    // Title + basic info
    canvas.drawText("Vaccine Report", 40f, y, titlePaint); y += 30f
    canvas.drawText("Child Name : ${report.childName}", 40f, y, normalPaint); y += 18f
    canvas.drawText("Parent Email: ${report.parentEmail}", 40f, y, normalPaint); y += 18f
    canvas.drawText("Date of Birth: ${report.dob}", 40f, y, normalPaint); y += 28f

    // Section title
    canvas.drawText("Vaccine History:", 40f, y, titlePaint); y += 24f

    // Header row
    canvas.drawText("Vaccine", 40f, y, normalPaint)
    canvas.drawText("Status", 220f, y, normalPaint)
    canvas.drawText("Given / Due", 350f, y, normalPaint)
    y += 16f

    // ✅ Single loop – this one only
    report.vaccines.forEach { v ->
        if (y > 800f) return@forEach  // basic overflow guard

        val dateText = when {
            !v.dateGiven.isNullOrBlank() && !v.dueDate.isNullOrBlank() ->
                "Given: ${v.dateGiven} / Due: ${v.dueDate}"
            !v.dateGiven.isNullOrBlank() ->
                "Given: ${v.dateGiven}"
            !v.dueDate.isNullOrBlank() ->
                "Due: ${v.dueDate}"
            else -> "-"
        }

        canvas.drawText(v.name, 40f, y, normalPaint)
        canvas.drawText(v.status, 220f, y, normalPaint)
        canvas.drawText(dateText, 350f, y, normalPaint)
        y += 16f
    }

    pdfDocument.finishPage(page)

    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(dir, "vaccine_report_${System.currentTimeMillis()}.pdf")

    FileOutputStream(file).use { out ->
        pdfDocument.writeTo(out)
    }

    pdfDocument.close()
    return file
}
