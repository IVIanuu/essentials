/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.coil

import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.Fetcher
import coil.key.Keyer
import coil.request.Options
import coil.size.Size
import coil.size.pxOrElse
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide

data class AppIcon(val packageName: String)

context(AppContext) @Provide class AppIconFetcherFactory : Fetcher.Factory<AppIcon> {
  override fun create(data: AppIcon, options: Options, imageLoader: ImageLoader) =
    Fetcher {
      val rawDrawable = packageManager.getApplicationIcon(data.packageName)
      val finalDrawable = when (val size = options.size) {
        Size.ORIGINAL -> rawDrawable
        else -> rawDrawable.toBitmap(
          width = size.width.pxOrElse { rawDrawable.intrinsicWidth },
          height = size.height.pxOrElse { rawDrawable.intrinsicHeight }
        )
          .toDrawable(resources)
      }
      DrawableResult(finalDrawable, false, DataSource.DISK)
    }
}

@Provide class AppIconKeyer : Keyer<AppIcon> {
  override fun key(data: AppIcon, options: Options) = data.packageName
}
