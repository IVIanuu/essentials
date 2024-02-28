/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.content.*
import android.graphics.drawable.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.core.content.res.*
import androidx.core.graphics.drawable.*
import com.ivianuu.injekt.*

@Provide class Resources(private val appContext: AppContext) {
  operator fun <T> invoke(id: Int, loader: ResourceLoader<T> = inject): T =
    loader.loadResource(appContext, id)

  operator fun <T> invoke(
    id: Int,
    vararg args: Any?,
    loader: ResourceLoaderWithArgs<T> = inject
  ): T = loader.loadResource(appContext, id, *args)
}

fun interface ResourceLoaderWithArgs<out T> {
  fun loadResource(context: Context, id: Int, vararg args: Any?): T

  @Provide companion object {
    @Provide val string =
      ResourceLoaderWithArgs { context, id, args -> context.getString(id, *args) }
  }
}

fun interface ResourceLoader<out T> {
  fun loadResource(context: Context, id: Int): T

  @Provide companion object {
    @Provide val bitmap = ResourceLoader { context, id -> context.getDrawable(id)!!.toBitmap() }
    @Provide val boolean = ResourceLoader { context, id -> context.resources.getBoolean(id) }
    @Provide val color = ResourceLoader { context, id -> Color(context.getColor(id)) }
    @Provide val dimension = ResourceLoader { context, id ->
      (context.resources.getDimensionPixelOffset(id) / context.resources.displayMetrics.density).dp
    }
    @Provide val drawable = ResourceLoader { context, id -> context.getDrawable(id)!! }
    @Provide val float =
      ResourceLoader { context, id -> ResourcesCompat.getFloat(context.resources, id) }
    @Provide val icon =
      ResourceLoader { context, id -> Icon.createWithResource(context, id) }
    @Provide val imageBitmap =
      ResourceLoader { context, id -> context.getDrawable(id)!!.toBitmap().asImageBitmap() }
    @Provide val typeface =
      ResourceLoader { context, id -> Typeface(ResourcesCompat.getFont(context, id)!!) }
    @Provide val frameworkTypeface =
      ResourceLoader { context, id -> ResourcesCompat.getFont(context, id)!! }
    @Provide val int = ResourceLoader { context, id -> context.resources.getInteger(id) }
    @Provide val intArray = ResourceLoader { context, id -> context.resources.getIntArray(id) }
    @Provide val string = ResourceLoader { context, id -> context.getString(id) }
    @Provide val stringArray =
      ResourceLoader { context, id -> context.resources.getStringArray(id) }
  }
}
