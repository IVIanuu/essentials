/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.compose.resources

import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.core.graphics.drawable.toBitmap
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Color
import androidx.ui.graphics.Image
import androidx.ui.graphics.toArgb
import com.ivianuu.essentials.ui.compose.image.BitmapImage
import com.ivianuu.essentials.util.drawable

fun colorResource(resId: Int) = effectOf<Color> {
    val context = +ambient(ContextAmbient)
    +memo { Color(context.getColor(resId)) }
}

fun drawableResource(resId: Int) = effectOf<Image> {
    val context = +ambient(ContextAmbient)
    return@effectOf +memo { BitmapImage(context.drawable(resId).toBitmap()) }
}

fun drawableResource(resId: Int, tint: Color? = null) = effectOf<Image> {
    val context = +ambient(ContextAmbient)
    return@effectOf +memo {
        val drawable = context.drawable(resId).mutate()
        if (tint != null) drawable.setTint(tint.toArgb())
        BitmapImage(drawable.toBitmap())
    }
}