package com.example.practica_desarrollomovil.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object FileUtils {
    /**
     * Copia una imagen desde un URI externo (galería) a la carpeta interna de la app.
     * Esto asegura que la imagen sea persistente incluso si el permiso temporal del URI expira.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "prod_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, "product_images").apply {
                if (!exists()) mkdirs()
            }
            val targetFile = File(file, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(targetFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
