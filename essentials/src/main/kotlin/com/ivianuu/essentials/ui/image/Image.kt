package com.ivianuu.essentials.ui.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.graphics.AndroidImageAccessor
import androidx.ui.graphics.Image

fun Drawable.toImage(): Image = toBitmap().toImage()

fun Bitmap.toImage(): Image = AndroidImageAccessor.createAndroidImage(this)

fun Image.toBitmap(): Bitmap = nativeImage