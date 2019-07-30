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

package com.ivianuu.essentials.apps.glide

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf

data class AppIcon(val packageName: String)

/**
 * Fetches images for [AppIcon]s
 */
@Inject
internal class AppIconFetcher(
    @Param private val app: AppIcon,
    private val packageManager: PackageManager
) : DataFetcher<Drawable> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Drawable>) {
        val drawable = packageManager.getApplicationIcon(app.packageName)
        if (drawable != null) {
            callback.onDataReady(drawable)
        } else {
            callback.onLoadFailed(Exception())
        }
    }

    override fun cleanup() {
    }

    override fun cancel() {
    }

    override fun getDataClass() = Drawable::class.java

    override fun getDataSource() = DataSource.LOCAL
}

/**
 * Model loader to load [AppIcon]s
 */
@Inject
internal class AppIconModelLoader(
    private val appIconFetcherProvider: Provider<AppIconFetcher>
) : ModelLoader<AppIcon, Drawable> {

    override fun buildLoadData(
        model: AppIcon,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Drawable> = ModelLoader.LoadData(
        ObjectKey(model), appIconFetcherProvider { parametersOf(model) }
    )

    override fun handles(model: AppIcon) = true

}

@Inject
internal class AppIconModelLoaderFactory(
    private val appIconModelLoaderProvider: Provider<AppIconModelLoader>
) : ModelLoaderFactory<AppIcon, Drawable> {

    override fun build(multiFactory: MultiModelLoaderFactory): AppIconModelLoader =
        appIconModelLoaderProvider()

    override fun teardown() {
    }
}