/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util.ext

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream


fun Bitmap.resize(width: Int, height: Int): Bitmap {
    val srcWidth = getWidth()

    val srcHeight = getHeight()

    val widthRatio = srcWidth.toFloat() / width.toFloat()
    val heightRatio = srcHeight.toFloat() / height.toFloat()

    return if (widthRatio < heightRatio) {
        val scaleBitmap = Bitmap.createScaledBitmap(
            this, width,
            Math.round(srcHeight / widthRatio), true
        )
        Bitmap.createBitmap(
            scaleBitmap, 0, Math.round((scaleBitmap.height - height) / 2.0f),
            width, height
        )
    } else {
        val scaleBitmap = Bitmap.createScaledBitmap(
            this, Math.round(srcWidth / heightRatio),
            height, true
        )

        Bitmap.createBitmap(
            scaleBitmap, Math.round((scaleBitmap.width - width) / 2.0f), 0,
            width, height
        )
    }
}

fun Bitmap.toBase64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val b = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun Bitmap.toByteArray(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(compressFormat, 100, stream)
    return stream.toByteArray()
}