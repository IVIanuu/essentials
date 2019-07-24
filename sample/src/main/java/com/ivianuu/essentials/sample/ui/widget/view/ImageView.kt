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

package com.ivianuu.essentials.sample.ui.widget.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import com.ivianuu.essentials.sample.ui.widget.lib.ContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget
import com.ivianuu.kommon.core.content.dp

fun Icon(
    iconDrawable: Drawable? = null,
    iconBitmap: Bitmap? = null,
    iconRes: Int? = null
) = StatelessWidget("Icon") {
    val size = (+ContextAmbient).dp(40).toInt()
    +SizedImageView(
        size = size,
        imageDrawable = iconDrawable,
        imageRes = iconRes,
        imageBitmap = iconBitmap
    )
}

fun Avatar(
    avatarDrawable: Drawable? = null,
    avatarBitmap: Bitmap? = null,
    avatarRes: Int? = null
) = StatelessWidget("Avatar") {
    val size = (+ContextAmbient).dp(40).toInt()
    +SizedImageView(
        size = size,
        imageDrawable = avatarDrawable,
        imageRes = avatarRes,
        imageBitmap = avatarBitmap
    )
}

fun SizedImageView(
    size: Int,
    imageDrawable: Drawable? = null,
    imageBitmap: Bitmap? = null,
    imageRes: Int? = null
) = StatelessWidget(id = "SizedImageView") {
    +Size(size) {
        +ImageView(
            imageDrawable = imageDrawable,
            imageBitmap = imageBitmap,
            imageRes = imageRes
        )
    }
}

fun ImageView(
    imageDrawable: Drawable? = null,
    imageBitmap: Bitmap? = null,
    imageRes: Int? = null
) = ViewWidget<AppCompatImageView> { view ->
    when {
        imageDrawable != null -> view.setImageDrawable(imageDrawable)
        imageBitmap != null -> view.setImageBitmap(imageBitmap)
        imageRes != null -> view.setImageResource(imageRes)
        else -> view.setImageDrawable(null)
    }
}