package com.kumbara.kala.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

object ImageUtils {
    fun loadBitmap(context: Context, imagePath: String): Bitmap? {
        return try {
            if (imagePath.startsWith("ASSET:")) {
                val assetName = imagePath.removePrefix("ASSET:")
                val resId = context.resources.getIdentifier(assetName, "drawable", context.packageName)
                if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
            } else if (imagePath.isNotBlank() && File(imagePath).exists()) {
                BitmapFactory.decodeFile(imagePath)
            } else null
        } catch (e: Exception) { null }
    }
    fun isValidPath(imagePath: String) = imagePath.isNotBlank() &&
        (imagePath.startsWith("ASSET:") || File(imagePath).exists())
}
