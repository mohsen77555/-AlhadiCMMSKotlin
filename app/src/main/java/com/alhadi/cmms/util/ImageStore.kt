package com.alhadi.cmms.util

import android.content.Context
import android.net.Uri
import android.graphics.BitmapFactory
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import java.io.File

/**
 * Stores work-order evidence photos inside the app's private files directory so they remain
 * available offline. Photos are captured directly from the device camera.
 */
object ImageStore {

    /** Creates the destination file (inside wo_photos/) the camera will write the capture to. */
    fun createCaptureFile(context: Context, orderId: Long): File {
        val dir = File(context.filesDir, "wo_photos").apply { mkdirs() }
        return File(dir, "wo_${orderId}_${System.currentTimeMillis()}.jpg")
    }

    /** FileProvider content URI for [file], grantable to the camera app. */
    fun uriFor(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    /** Decodes a stored photo (down-sampled for thumbnails) into an [ImageBitmap], or null. */
    fun decode(path: String, sampleSize: Int = 4): ImageBitmap? = try {
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        BitmapFactory.decodeFile(path, options)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }

    fun delete(path: String) {
        runCatching { File(path).takeIf { it.exists() }?.delete() }
    }

    /** Copies a picked content [src] into the app's private [subdir] and returns the new file path. */
    fun importToFiles(context: Context, subdir: String, src: Uri, displayName: String?): String {
        val dir = File(context.filesDir, subdir).apply { mkdirs() }
        val safeName = (displayName ?: "file_${System.currentTimeMillis()}").replace(Regex("[^A-Za-z0-9._-]"), "_")
        val dest = File(dir, "${System.currentTimeMillis()}_$safeName")
        context.contentResolver.openInputStream(src)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: throw IllegalStateException("تعذّر قراءة الملف")
        return dest.absolutePath
    }

    /** Human-readable name of a content [uri], if available. */
    fun queryDisplayName(context: Context, uri: Uri): String? {
        var name: String? = null
        runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && c.moveToFirst()) name = c.getString(idx)
            }
        }
        return name
    }

    fun isImagePath(path: String): Boolean =
        path.substringAfterLast('.', "").lowercase() in setOf("jpg", "jpeg", "png", "webp", "gif", "bmp")
}
