/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.apps.coil

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
import com.ivianuu.essentials.coil.FetcherBinding
import com.ivianuu.injekt.android.AppResources

data class AppIcon(val packageName: String)

@FetcherBinding
class AppIconFetcher(
    private val packageManager: PackageManager,
    private val resources: AppResources
) : Fetcher<AppIcon> {
    override fun key(data: AppIcon): String? = data.packageName

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
                .toDrawable(resources)
        }
        return DrawableResult(finalDrawable, false, DataSource.DISK)
    }
}
