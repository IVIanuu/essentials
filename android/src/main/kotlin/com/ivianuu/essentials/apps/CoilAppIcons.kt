/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps

import androidx.core.graphics.drawable.*
import coil.*
import coil.decode.*
import coil.fetch.*
import coil.key.*
import coil.request.*
import coil.size.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

data class AppIcon(val packageName: String)

@Provide class AppIconFetcherFactory(private val appContext: AppContext) :
  Fetcher.Factory<AppIcon> {
  override fun create(data: AppIcon, options: Options, imageLoader: ImageLoader) =
    Fetcher {
      val rawDrawable = appContext.packageManager.getApplicationIcon(data.packageName)
      val finalDrawable = when (val size = options.size) {
        Size.ORIGINAL -> rawDrawable
        else -> rawDrawable.toBitmap(
          width = size.width.pxOrElse { rawDrawable.intrinsicWidth },
          height = size.height.pxOrElse { rawDrawable.intrinsicHeight }
        )
          .toDrawable(appContext.resources)
      }
      DrawableResult(finalDrawable, false, DataSource.DISK)
    }
}

@Provide class AppIconKeyer : Keyer<AppIcon> {
  override fun key(data: AppIcon, options: Options) = data.packageName
}
