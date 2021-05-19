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

package com.ivianuu.essentials

import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.core.content.res.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.ui.image.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

typealias BitmapResourceProvider = (Int) -> ImageBitmap

@Provide fun bitmapResourceProvider(context: AppContext): BitmapResourceProvider = { id ->
  context.getDrawable(id)!!.toBitmap().toImageBitmap()
}

typealias BooleanResourceProvider = (Int) -> Boolean

@Provide fun booleanResourceProvider(context: AppContext): BooleanResourceProvider = { id ->
  context.resources.getBoolean(id)
}

typealias ColorResourceProvider = (Int) -> Color

@Provide fun colorResourceProvider(context: AppContext): ColorResourceProvider = { id ->
  Color(context.getColor(id))
}

typealias DimensionResourceProvider = (Int) -> Dp

@Provide fun dimensionResourceProvider(context: AppContext): DimensionResourceProvider = { id ->
  with(Density(context)) {
    context.resources.getDimension(id).toInt().toDp()
  }
}

typealias DrawableResourceProvider = (Int) -> ImageBitmap

@Provide inline val BitmapResourceProvider.drawableResourceProvider: DrawableResourceProvider
  get() = this

typealias FloatResourceProvider = (Int) -> Float

@Provide fun floatResourceProvider(context: AppContext): FloatResourceProvider = { id ->
  ResourcesCompat.getFloat(context.resources, id)
}

typealias FontResourceProvider = (Int) -> Font

@Provide val fontResourceProvider: FontResourceProvider = { id -> Font(id) }

typealias IntResourceProvider = (Int) -> Int

@Provide fun intResourceProvider(context: AppContext): IntResourceProvider = { id ->
  context.resources.getInteger(id)
}

typealias IntArrayResourceProvider = (Int) -> IntArray

@Provide fun intArrayResourceProvider(context: AppContext): IntArrayResourceProvider = { id ->
  context.resources.getIntArray(id)
}

typealias StringResourceProvider = (Int, List<Any?>) -> String

@Provide fun stringResourceProvider(context: AppContext): StringResourceProvider = { id, args ->
  context.getString(id, *args.toTypedArray())
}

typealias StringArrayResourceProvider = (Int) -> Array<String>

@Provide fun stringArrayResourceProvider(context: AppContext): StringArrayResourceProvider = { id ->
  context.resources.getStringArray(id)
}
