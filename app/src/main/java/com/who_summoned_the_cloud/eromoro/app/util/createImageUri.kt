package com.who_summoned_the_cloud.eromoro.app.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"

    val storageDir: File? = context.cacheDir

    val imageFile = File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir,
    )

    val authority = "${context.packageName}.provider"

    return FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        authority,
        imageFile,
    )
}