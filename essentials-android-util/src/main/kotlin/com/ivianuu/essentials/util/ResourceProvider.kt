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
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext

interface ResourceProvider {
    fun bitmap(id: Int): ImageBitmap

    fun boolean(id: Int): Boolean

    fun color(id: Int): Color

    fun dimension(id: Int): Dp

    fun drawable(id: Int): ImageBitmap

    fun float(id: Int): Float

    fun font(id: Int): Font

    fun int(id: Int): Int

    fun intArray(id: Int): IntArray

    fun string(id: Int): String

    fun string(id: Int, vararg arguments: Any?): String

    fun stringArray(id: Int): Array<String>
}

@Given
class ResourceProviderImpl(@Given private val appContext: AppContext) : ResourceProvider {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun bitmap(id: Int): ImageBitmap =
        appContext.getDrawable(id)!!.toImageBitmap()

    override fun boolean(id: Int): Boolean =
        appContext.resources.getBoolean(id)

    override fun color(id: Int): Color =
        Color(appContext.getColor(id))

    override fun dimension(id: Int): Dp = with(Density(appContext)) {
        appContext.resources.getDimension(id).toInt().toDp()
    }

    override fun drawable(id: Int): ImageBitmap =
        bitmap(id)

    override fun float(id: Int): Float =
        ResourcesCompat.getFloat(appContext.resources, id)

    override fun font(id: Int): Font = Font(id)

    override fun int(id: Int): Int =
        appContext.resources.getInteger(id)

    override fun intArray(id: Int): IntArray = appContext.resources.getIntArray(id)

    override fun string(id: Int): String =
        appContext.getString(id)

    override fun string(id: Int, vararg arguments: Any?): String =
        appContext.getString(id, *arguments)

    override fun stringArray(id: Int): Array<String> = appContext.resources.getStringArray(id)
}
