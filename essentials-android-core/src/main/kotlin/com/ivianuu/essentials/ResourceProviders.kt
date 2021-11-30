/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials

import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.core.content.res.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.ui.image.*
import com.ivianuu.injekt.*

inline fun <T> loadResource(
  id: Int,
  @Inject loader: ResourceLoader<T>,
  provider: ResourceProvider
): T = provider(id)

inline fun <T> loadResource(
  id: Int,
  vararg args: Any?,
  @Inject loader: ResourceLoaderWithArgs<T>,
  provider: ResourceProvider
): T = provider(id, *args)

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
  ): T = loader(context, id, *args)
}

fun interface ResourceLoaderWithArgs<out T> {
  operator fun invoke(context: AppContext, id: Int, vararg args: Any?): T

  companion object {
    @Provide val string = ResourceLoaderWithArgs { context, id, args ->
      context.getString(id, *args)
    }
  }
}

fun interface ResourceLoader<out T> {
  operator fun invoke(context: AppContext, id: Int): T

  companion object {
    @Provide val bitmap = ResourceLoader { context, id ->
      context.getDrawable(id)!!.toBitmap()
    }
    @Provide val boolean = ResourceLoader { context, id -> context.resources.getBoolean(id) }
    @Provide val color = ResourceLoader { context, id -> Color(context.getColor(id)) }
    @Provide val dimension = ResourceLoader { context, id ->
      (context.resources.getDimensionPixelOffset(id) / context.resources.displayMetrics.density).dp
    }
    @Provide val drawable = ResourceLoader { context, id ->
      context.getDrawable(id)!!
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
    @Provide val frameworkTypeface = ResourceLoader { context, id ->
      ResourcesCompat.getFont(context, id)!!
    }
    @Provide val int = ResourceLoader { context, id -> context.resources.getInteger(id) }
    @Provide val intArray = ResourceLoader { context, id -> context.resources.getIntArray(id) }
    @Provide val string = ResourceLoader { context, id -> context.getString(id) }
    @Provide val stringArray = ResourceLoader { context, id ->
      context.resources.getStringArray(id)
    }
  }
}
