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

interface ResourceProvider {
  operator fun <T> invoke(id: Int, @Inject loader: ResourceLoader<T>): T

  operator fun <T> invoke(id: Int, vararg args: Any?, @Inject loader: ResourceLoaderWithArgs<T>): T
}

@Provide class ResourceProviderImpl(private val context: AppContext) : ResourceProvider {
  override fun <T> invoke(id: Int, @Inject loader: ResourceLoader<T>): T =
    loader(context, id)

  override fun <T> invoke(
    id: Int,
    vararg args: Any?,
    @Inject loader: ResourceLoaderWithArgs<T>
  ): T =
    loader(context, id, *args)
}

fun interface ResourceLoaderWithArgs<T> {
  operator fun invoke(context: AppContext, id: Int, vararg args: Any?): T

  companion object {
    @Provide val string = ResourceLoaderWithArgs { context, id, args ->
      context.getString(id, *args)
    }
  }
}

fun interface ResourceLoader<T> {
  operator fun invoke(context: AppContext, id: Int): T

  companion object {
    @Provide val boolean = ResourceLoader { context, id -> context.resources.getBoolean(id) }
    @Provide val color = ResourceLoader { context, id -> Color(context.getColor(id)) }
    @Provide val dimension = ResourceLoader { context, id ->
      with(Density(context)) {
        context.resources.getDimension(id).toInt().toDp()
      }
    }
    @Provide val float = ResourceLoader { context, id ->
      ResourcesCompat.getFloat(context.resources, id)
    }
    @Provide val imageBitmap = ResourceLoader { context, id ->
      context.getDrawable(id)!!.toBitmap().toImageBitmap()
    }
    @Provide val typeface = ResourceLoader { context, id ->
      Typeface(ResourcesCompat.getFont(context, id)!!)
    }
    @Provide val int = ResourceLoader { context, id -> context.resources.getInteger(id) }
    @Provide val intArray = ResourceLoader { context, id -> context.resources.getIntArray(id) }
    @Provide val string = ResourceLoader { context, id -> context.getString(id) }
    @Provide val stringArray = ResourceLoader { context, id ->
      context.resources.getStringArray(id)
    }
  }
}
