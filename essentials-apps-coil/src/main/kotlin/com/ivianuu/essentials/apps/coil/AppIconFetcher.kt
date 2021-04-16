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

import android.content.pm.*
import androidx.core.graphics.drawable.*
import coil.bitmap.*
import coil.decode.*
import coil.fetch.*
import coil.size.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

data class AppIcon(val packageName: String)

@Given
class AppIconFetcher(
    @Given private val packageManager: PackageManager,
    @Given private val resources: AppResources
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
                .toDrawable(resources)
        }
        return DrawableResult(finalDrawable, false, DataSource.DISK)
    }
}
