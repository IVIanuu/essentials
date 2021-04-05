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

package com.ivianuu.essentials.util

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext

@Given
class ResourceProvider(@Given private val appContext: AppContext) {
    @SuppressLint("UseCompatLoadingForDrawables")
    fun bitmap(id: Int): ImageBitmap =
        appContext.getDrawable(id)!!.toBitmap().toImageBitmap()

    fun boolean(id: Int): Boolean =
        appContext.resources.getBoolean(id)

    fun color(id: Int): Color =
        Color(appContext.getColor(id))

    fun dimension(id: Int): Dp = with(Density(appContext)) {
        appContext.resources.getDimension(id).toInt().toDp()
    }

    fun drawable(id: Int): ImageBitmap =
        bitmap(id)

    fun float(id: Int): Float =
        ResourcesCompat.getFloat(appContext.resources, id)

    fun font(id: Int): Font = Font(id)

    fun int(id: Int): Int =
        appContext.resources.getInteger(id)

    fun intArray(id: Int): IntArray = appContext.resources.getIntArray(id)

    fun string(id: Int): String =
        appContext.getString(id)

    fun string(id: Int, vararg arguments: Any?): String =
        appContext.getString(id, *arguments)

    fun stringArray(id: Int): Array<String> = appContext.resources.getStringArray(id)
}
