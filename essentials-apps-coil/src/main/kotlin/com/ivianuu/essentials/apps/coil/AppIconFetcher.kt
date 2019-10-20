/*
 * Copyright 2019 Manuel Wrage
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
import coil.bitmappool.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.Size
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.withContext

data class AppIcon(val packageName: String)

@Inject
internal class AppIconFetcher(
    private val dispatchers: AppDispatchers,
    private val packageManager: PackageManager
) : Fetcher<AppIcon> {

    override fun key(data: AppIcon): String? = data.packageName

    override suspend fun fetch(
        pool: BitmapPool,
        data: AppIcon,
        size: Size,
        options: Options
    ): FetchResult = withContext(dispatchers.io) {
        val drawable = packageManager.getApplicationIcon(data.packageName)
        return@withContext DrawableResult(drawable, false, DataSource.NETWORK)
    }

}