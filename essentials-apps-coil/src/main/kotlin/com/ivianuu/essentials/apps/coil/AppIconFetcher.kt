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
import androidx.compose.Immutable
import coil.bitmappool.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.Size
import com.ivianuu.essentials.coil.FetcherBinding
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext

@Immutable
data class AppIcon(val packageName: String)

@Given
internal class AppIconFetcher : Fetcher<AppIcon> {

    override fun key(data: AppIcon): String? = data.packageName

    override suspend fun fetch(
        pool: BitmapPool,
        data: AppIcon,
        size: Size,
        options: Options
    ): FetchResult = withContext(dispatchers.io) {
        val drawable = given<PackageManager>().getApplicationIcon(data.packageName)
        return@withContext DrawableResult(drawable, false, DataSource.DISK)
    }

    companion object {
        @SetElements(ApplicationComponent::class)
        fun appIconFetcherIntoMap(): Set<FetcherBinding<*>> = setOf(
            FetcherBinding(
                given<AppIconFetcher>(),
                AppIcon::class
            )
        )
    }
}
