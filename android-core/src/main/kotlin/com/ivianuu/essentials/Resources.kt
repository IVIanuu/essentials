/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

import android.graphics.drawable.Icon
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.context

@Stable interface Resources {
  context(ResourceLoader<T>) fun <T> resource(id: Int): T

  context(ResourceLoaderWithArgs<T>) fun <T> resourceWith(id: Int, vararg args: Any?): T
}

context(AppContext) @Provide class ResourcesImpl : Resources {
  context(ResourceLoader<T>) override fun <T> resource(id: Int): T =
    this@ResourceLoader.load(id)

  context(ResourceLoaderWithArgs<T>) override fun <T> resourceWith(id: Int, vararg args: Any?): T =
    this@ResourceLoaderWithArgs.load(id, *args)
}

fun interface ResourceLoaderWithArgs<out T> {
  context(AppContext) fun load(id: Int, vararg args: Any?): T

  @Provide companion object {
    @Provide val string = ResourceLoaderWithArgs { id, args -> getString(id, *args) }
  }
}

fun interface ResourceLoader<out T> {
  context(AppContext) fun load(id: Int): T

  @Provide companion object {
    @Provide val bitmap = ResourceLoader { getDrawable(it)!!.toBitmap() }
    @Provide val boolean = ResourceLoader { resources.getBoolean(it) }
    @Provide val color = ResourceLoader { Color(getColor(it)) }
    @Provide val dimension = ResourceLoader {
      (resources.getDimensionPixelOffset(it) / resources.displayMetrics.density).dp
    }
    @Provide val drawable = ResourceLoader { getDrawable(it)!! }
    @Provide val float = ResourceLoader { ResourcesCompat.getFloat(resources, it) }
    @Provide val icon = ResourceLoader { Icon.createWithResource(context(), it) }
    @Provide val imageBitmap = ResourceLoader { getDrawable(it)!!.toBitmap().toImageBitmap() }
    @Provide val typeface = ResourceLoader { Typeface(ResourcesCompat.getFont(context(), it)!!) }
    @Provide val frameworkTypeface = ResourceLoader {ResourcesCompat.getFont(context(), it)!! }
    @Provide val int = ResourceLoader { resources.getInteger(it) }
    @Provide val intArray = ResourceLoader { resources.getIntArray(it) }
    @Provide val string = ResourceLoader { getString(it) }
    @Provide val stringArray = ResourceLoader { resources.getStringArray(it) }
  }
}
