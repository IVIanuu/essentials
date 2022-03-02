/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.image

import android.graphics.*
import androidx.compose.ui.graphics.*

fun Bitmap.toImageBitmap(): ImageBitmap =
  AndroidImageBitmapAccessor.createAndroidImageBitmap(this)

fun ImageBitmap.toBitmap(): Bitmap = AndroidImageBitmapAccessor.getBitmap(this)
