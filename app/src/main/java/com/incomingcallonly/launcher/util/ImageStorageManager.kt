package com.incomingcallonly.launcher.util

import android.content.Context
import android.net.Uri
import kotlin.math.min
import androidx.core.net.toUri
import androidx.core.graphics.scale
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val photosDir = File(context.filesDir, "contact_photos")

    init {
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
    }

    fun saveImageLocally(uri: Uri): String? {
        return try {
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val destFile = File(photosDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                val originalBitmap = android.graphics.BitmapFactory.decodeStream(input)
                
                // Resize logic
                val maxDimension = 512
                val ratio = min(
                    maxDimension.toFloat() / originalBitmap.width,
                    maxDimension.toFloat() / originalBitmap.height
                )
                val width = (originalBitmap.width * ratio).toInt()
                val height = (originalBitmap.height * ratio).toInt()
                
                val resizedBitmap = originalBitmap.scale(width, height, true)

                FileOutputStream(destFile).use { output ->
                     resizedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, output)
                }
                
                if (originalBitmap != resizedBitmap) {
                    originalBitmap.recycle()
                }
                resizedBitmap.recycle()
            }
            Uri.fromFile(destFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImage(uriString: String?) {
        if (uriString == null) return
        try {
            val uri = uriString.toUri()
            if (uri.scheme == "file") {
                val file = File(uri.path ?: return)
                if (file.exists() && file.parentFile?.name == "contact_photos") {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
