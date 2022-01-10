/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.apps.ui

import android.content.pm.*
import androidx.core.graphics.drawable.*
import coil.bitmap.*
import coil.decode.*
import coil.fetch.*
import coil.size.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

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
