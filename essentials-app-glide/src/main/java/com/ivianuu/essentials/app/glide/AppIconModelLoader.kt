/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.app.glide

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
import com.ivianuu.assistedinject.Assisted
import com.ivianuu.assistedinject.AssistedInject
import com.ivianuu.essentials.util.ext.coroutinesIo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

data class AppIcon(val packageName: String)

/**
 * Fetches images for [AppIcon]s
 */
class AppIconFetcher @AssistedInject constructor(
    @Assisted private val app: AppIcon,
    private val packageManager: PackageManager
) :
    DataFetcher<Drawable> {

    private var job: Job? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Drawable>) {
        job = GlobalScope.launch(coroutinesIo) {
            val drawable = packageManager.getApplicationIcon(app.packageName)
            if (drawable != null) {
                callback.onDataReady(drawable)
            } else {
                callback.onLoadFailed(Exception())
            }
        }
    }

    override fun cleanup() {
    }

    override fun cancel() {
        job?.cancel()
    }

    override fun getDataClass() = Drawable::class.java

    override fun getDataSource() = DataSource.LOCAL
}

/**
 * Model loader to load [AppIcon]s
 */
class AppIconModelLoader @Inject constructor(
    private val appIconFetcherFactory: AppIconFetcherFactory
) : ModelLoader<AppIcon, Drawable> {

    override fun buildLoadData(
        model: AppIcon,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Drawable> = ModelLoader.LoadData(
        ObjectKey(model), appIconFetcherFactory.create(model)
    )

    override fun handles(model: AppIcon) = true

    class Factory @Inject constructor(
        private val appIconModelLoaderProvider: Provider<AppIconModelLoader>
    ) : ModelLoaderFactory<AppIcon, Drawable> {

        override fun build(multiFactory: MultiModelLoaderFactory): AppIconModelLoader =
            appIconModelLoaderProvider.get()

        override fun teardown() {
        }
    }
}