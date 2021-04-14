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

typealias LoadBitmapResourceUseCase = (Int) -> ImageBitmap

@Given
fun loadBitmapResourceUseCase(@Given context: AppContext): LoadBitmapResourceUseCase = { id ->
    context.getDrawable(id)!!.toBitmap().toImageBitmap()
}

typealias LoadBooleanResourceUseCase = (Int) -> Boolean

@Given
fun loadBooleanResourceUseCase(@Given context: AppContext): LoadBooleanResourceUseCase = { id ->
    context.resources.getBoolean(id)
}

typealias LoadColorResourceUseCase = (Int) -> Color

@Given
fun loadColorResourceUseCase(@Given context: AppContext): LoadColorResourceUseCase = { id ->
    Color(context.getColor(id))
}

typealias LoadDimensionResourceUseCase = (Int) -> Dp

@Given
fun loadDimensionResourceUseCase(@Given context: AppContext): LoadDimensionResourceUseCase = { id ->
    with(Density(context)) {
        context.resources.getDimension(id).toInt().toDp()
    }
}

typealias LoadDrawableResourceUseCase = (Int) -> ImageBitmap

@Given
inline val LoadBitmapResourceUseCase.loadDrawableResourceUseCase: LoadDrawableResourceUseCase
    get() = this

typealias LoadFloatResourceUseCase = (Int) -> Float

@Given
fun loadFloatResourceUseCase(@Given context: AppContext): LoadFloatResourceUseCase = { id ->
    ResourcesCompat.getFloat(context.resources, id)
}

typealias LoadFontResourceUseCase = (Int) -> Font

@Given
val loadFontResourceUseCase: LoadFontResourceUseCase = { id -> Font(id) }

typealias LoadIntResourceUseCase = (Int) -> Int

@Given
fun loadIntResourceUseCase(@Given context: AppContext): LoadIntResourceUseCase = { id ->
    context.resources.getInteger(id)
}

typealias LoadIntArrayResourceUseCase = (Int) -> IntArray

@Given
fun loadIntArrayResourceUseCase(@Given context: AppContext): LoadIntArrayResourceUseCase = { id ->
    context.resources.getIntArray(id)
}

typealias LoadStringResourceUseCase = (Int, List<Any?>) -> String

@Given
fun loadStringResourceUseCase(@Given context: AppContext): LoadStringResourceUseCase = { id, args ->
    context.getString(id, args)
}

typealias LoadStringArrayResourceUseCase = (Int) -> Array<String>

@Given
fun loadStringArrayResourceUseCase(@Given context: AppContext): LoadStringArrayResourceUseCase = { id ->
    context.resources.getStringArray(id)
}
