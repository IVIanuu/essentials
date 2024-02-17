/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

@Provide class Resources(private val appContext: AppContext) {
  operator fun <T> invoke(id: Int, @Inject loader: ResourceLoader<T>): T = loader(appContext, id)

  operator fun <T> invoke(
    id: Int,
    vararg args: Any?,
    @Inject loader: ResourceLoaderWithArgs<T>
  ): T = loader(appContext, id, *args)
}

fun interface ResourceLoaderWithArgs<out T> {
  operator fun invoke(context: Context, id: Int, vararg args: Any?): T

  @Provide companion object {
    @Provide val string =
      ResourceLoaderWithArgs { context, id, args -> context.getString(id, *args) }
  }
}

fun interface ResourceLoader<out T> {
  operator fun invoke(context: Context, id: Int): T

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
