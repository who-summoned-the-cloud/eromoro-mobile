package com.who_summoned_the_cloud.eromoro.app.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver

    val mimeType = contentResolver.getType(uri)
    val extension = MimeTypeMap
        .getSingleton().getExtensionFromMimeType(mimeType) ?: "tmp"

    val fileName = "temp_file_${UUID.randomUUID()}.$extension"

    val tempFile = File(context.cacheDir, fileName)

    try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            return null
        }

        val outputStream = FileOutputStream(tempFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return tempFile

    } catch (e: Exception) {
        e.printStackTrace()
        tempFile.delete()
        return null
    }
}