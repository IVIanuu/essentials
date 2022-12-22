/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject

interface ResourceProvider {
  context(ResourceLoader<T>) fun <T> loadResource(id: Int): T

  context(ResourceLoaderWithArgs<T>) fun <T> loadResourceWithArgs(id: Int, vararg args: Any?): T
}

context(AppContext) @Provide class ResourceProviderImpl : ResourceProvider {
  context(ResourceLoader<T>) override fun <T> loadResource(id: Int): T =
    with(this@AppContext as Context) { load(id) }

  context(ResourceLoaderWithArgs<T>) override fun <T> loadResourceWithArgs(
    id: Int,
    vararg args: Any?
  ): T =
    with(this@AppContext as Context) { load(id, *args) }
}

// todo make fun interface once fixed
interface ResourceLoaderWithArgs<out T> {
  context(Context) fun load(id: Int, vararg args: Any?): T

  companion object {
    @Provide val string = ResourceLoaderWithArgs { id, args -> getString(id, *args) }
  }
}

inline fun <T> ResourceLoaderWithArgs(
  crossinline function: context(Context) (Int, Array<out Any?>) -> T
): ResourceLoaderWithArgs<T> = object : ResourceLoaderWithArgs<T> {
  context(Context) override fun load(id: Int, vararg args: Any?) = function.invoke(id, args)
}

// todo make fun interface once fixed
interface ResourceLoader<out T> {
  context(Context) fun load(id: Int): T

  companion object {
    @Provide val bitmap = ResourceLoader { getDrawable(it)!!.toBitmap() }
    @Provide val boolean = ResourceLoader { resources.getBoolean(it) }
    @Provide val color = ResourceLoader { Color(getColor(it)) }
    @Provide val dimension = ResourceLoader {
      (resources.getDimensionPixelOffset(it) / resources.displayMetrics.density).dp
    }
    @Provide val drawable = ResourceLoader { getDrawable(it)!! }
    @Provide val float = ResourceLoader { ResourcesCompat.getFloat(resources, it) }
    @Provide val imageBitmap = ResourceLoader { getDrawable(it)!!.toBitmap().toImageBitmap() }
    @Provide val typeface = ResourceLoader { Typeface(ResourcesCompat.getFont(inject(), it)!!) }
    @Provide val frameworkTypeface = ResourceLoader { ResourcesCompat.getFont(inject(), it)!! }
    @Provide val int = ResourceLoader { resources.getInteger(it) }
    @Provide val intArray = ResourceLoader { resources.getIntArray(it) }
    @Provide val string = ResourceLoader { getString(it) }
    @Provide val stringArray = ResourceLoader { resources.getStringArray(it) }
  }
}

inline fun <T> ResourceLoader(
  crossinline function: context(Context) (Int) -> T
): ResourceLoader<T> = object : ResourceLoader<T> {
  context(Context) override fun load(id: Int): T = function(id)
}
