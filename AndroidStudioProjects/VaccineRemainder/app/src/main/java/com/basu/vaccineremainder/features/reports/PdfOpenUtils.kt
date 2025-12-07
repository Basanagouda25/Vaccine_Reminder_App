package com.basu.vaccineremainder.features.reports

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

fun openPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Open vaccine report"))
}
