/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.apps

import android.app.*
import androidx.core.graphics.drawable.*
import coil.*
import coil.decode.*
import coil.fetch.*
import coil.key.*
import coil.request.*
import coil.size.*
import injekt.*

data class AppIcon(val packageName: String)

@Provide fun appIconFetcherFactory(context: Application) = object : Fetcher.Factory<AppIcon> {
  override fun create(data: AppIcon, options: Options, imageLoader: ImageLoader) = Fetcher {
    val rawDrawable = context.packageManager.getApplicationIcon(data.packageName)
    val finalDrawable = when (val size = options.size) {
      Size.ORIGINAL -> rawDrawable
      else -> rawDrawable.toBitmap(
        width = size.width.pxOrElse { rawDrawable.intrinsicWidth },
        height = size.height.pxOrElse { rawDrawable.intrinsicHeight }
      )
        .toDrawable(context.resources)
    }
    DrawableResult(finalDrawable, false, DataSource.DISK)
  }
}

@Provide fun appIconKeyer() = Keyer<AppIcon> { data, _ -> data.packageName }
