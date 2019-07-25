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

package com.ivianuu.essentials.sample.ui.widget.builder

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext

fun BuildContext.ImageView(block: BuildView<AppCompatImageView>) = View(block)

fun <V : ImageView> ViewWidgetBuilder<V>.image(
    drawable: Drawable? = null,
    bitmap: Bitmap? = null,
    res: Int? = null
) {
    updateView {
        when {
            drawable != null -> it.setImageDrawable(drawable)
            bitmap != null -> it.setImageBitmap(bitmap)
            res != null -> it.setImageResource(res)
            else -> it.setImageDrawable(null)
        }
    }
}