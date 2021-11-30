/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.ui

import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.OriginalSize
import coil.size.PixelSize
import coil.size.Size
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide

data class AppIcon(val packageName: String)

@Provide class AppIconFetcher(
  private val context: AppContext,
  private val packageManager: PackageManager
) : Fetcher<AppIcon> {
  override fun key(data: AppIcon): String = data.packageName

  override suspend fun fetch(
    pool: BitmapPool,
    data: AppIcon,
    size: Size,
    options: Options,
  ): FetchResult {
    val rawDrawable = packageManager.getApplicationIcon(data.packageName)
    val finalDrawable = when (size) {
      OriginalSize -> rawDrawable
      is PixelSize -> rawDrawable.toBitmap(width = size.width, height = size.height)
        .toDrawable(context.resources)
    }
    return DrawableResult(finalDrawable, false, DataSource.DISK)
  }
}
