package com.alhadi.cmms.util

import android.content.Context
import android.net.Uri
import android.graphics.BitmapFactory
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
}
