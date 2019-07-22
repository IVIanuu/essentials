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

package com.ivianuu.essentials.sample.ui.widget2.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.ivianuu.essentials.sample.ui.widget2.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.kommon.core.content.dp

open class Icon(
    val iconDrawable: Drawable? = null,
    val iconBitmap: Bitmap? = null,
    val iconRes: Int? = null, key: Any?
) : StatelessWidget(key) {

    override fun build(context: BuildContext): Widget {
        val size = AndroidContextAmbient(context).dp(24).toInt()
        return Size(
            width = size,
            height = size,
            child = ImageViewWidget(
                imageDrawable = iconDrawable,
                imageBitmap = iconBitmap,
                imageRes = iconRes
            )
        )
    }

}

open class Avatar(
    val avatarDrawable: Drawable? = null,
    val avatarBitmap: Bitmap? = null,
    val avatarRes: Int? = null,
    key: Any? = null
) : StatelessWidget(key) {

    override fun build(context: BuildContext): Widget {
        val size = AndroidContextAmbient(context).dp(40).toInt()
        return Size(
            width = size,
            height = size,
            child = ImageViewWidget(
                imageDrawable = avatarDrawable,
                imageBitmap = avatarBitmap,
                imageRes = avatarRes
            )
        )
    }

}

open class ImageViewWidget(
    val imageDrawable: Drawable? = null,
    val imageBitmap: Bitmap? = null,
    val imageRes: Int? = null,
    key: Any? = null
) : ViewWidget<ImageView>(key) {
    override fun createView(context: BuildContext): ImageView =
        AppCompatImageView(AndroidContextAmbient(context))

    override fun updateView(context: BuildContext, view: ImageView) {
        super.updateView(context, view)
        when {
            imageDrawable != null -> view.setImageDrawable(imageDrawable)
            imageBitmap != null -> view.setImageBitmap(imageBitmap)
            imageRes != null -> view.setImageResource(imageRes)
            else -> view.setImageDrawable(null)
        }
    }
}