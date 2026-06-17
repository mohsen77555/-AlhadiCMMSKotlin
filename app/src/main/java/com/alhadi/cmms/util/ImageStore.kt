package com.alhadi.cmms.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

/**
 * Stores work-order evidence photos inside the app's private files directory so they remain
 * available offline and survive content-URI permission expiry.
 */
object ImageStore {

    /** Copies the picked image [uri] into internal storage and returns the absolute path, or null. */
    fun copyToInternal(context: Context, uri: Uri, orderId: Long): String? = try {
        val dir = File(context.filesDir, "wo_photos").apply { mkdirs() }
        val file = File(dir, "wo_${orderId}_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        if (file.exists() && file.length() > 0) file.absolutePath else null
    } catch (e: Exception) {
        null
    }

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
}
