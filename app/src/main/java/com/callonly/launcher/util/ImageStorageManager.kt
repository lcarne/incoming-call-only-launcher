package com.callonly.launcher.util

import android.content.Context
import android.net.Uri
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
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
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
            val uri = Uri.parse(uriString)
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
