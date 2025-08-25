package com.mno.jamscope.util

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import coil3.imageLoader
import java.io.File

/**
 * Salva uma imagem do cache de disco do Coil na galeria do dispositivo.
 *
 * @param context O contexto da aplicação.
 * @param imageUrl A URL da imagem a ser salva, usada como chave do cache.
 * @return `true` se a imagem foi salva com sucesso, `false` caso contrário.
 */
fun saveImageFromCoilCache(context: Context, imageUrl: String): Boolean {
    val imageLoader = context.imageLoader //image loader do coil
    val cacheKey = imageUrl //a chave no cache é a própria URL da imagem

    val snapshot = imageLoader.diskCache?.openSnapshot(cacheKey) //obtem a imagem do cache

    if (snapshot == null) {
        return false
    }

    return try {
        val cacheFile: File = snapshot.data.toFile()

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true //ler so os metadados sem carregar a imagem na memória
        }
        BitmapFactory.decodeFile(cacheFile.absolutePath, options)
        val mimeType = options.outMimeType ?: "image/jpeg"

        val extension = when (mimeType) {
            "image/gif" -> "gif"
            else -> "jpg"
        }
        val displayName = "IMG_${System.currentTimeMillis()}.$extension"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Jamscope")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                cacheFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream!!)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        }

        snapshot.close()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        snapshot.close()
        false
    }
}