/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.AndroidImageBitmapAccessor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.graphics.drawable.toBitmap

fun Drawable.toImageBitmap(): ImageBitmap = toBitmap().toImageBitmap()

fun Bitmap.toImageBitmap(): ImageBitmap =
    AndroidImageBitmapAccessor.createAndroidImageBitmap(this)

fun ImageBitmap.toBitmap(): Bitmap = AndroidImageBitmapAccessor.getBitmap(this)
