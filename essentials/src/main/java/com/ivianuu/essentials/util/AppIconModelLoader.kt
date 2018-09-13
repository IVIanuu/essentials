package com.ivianuu.essentials.util

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
import com.ivianuu.essentials.util.coroutines.AppCoroutineDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

data class AppIcon(val packageName: String)

/**
 * Fetches images for [AppIcon]'s
 */
class AppIconFetcher @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val packageManager: PackageManager
) : DataFetcher<Drawable> {

    lateinit var app: AppIcon

    private lateinit var job: Job

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Drawable>) {
        job = GlobalScope.launch(dispatchers.io) {
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
        job.cancel()
    }

    override fun getDataClass() = Drawable::class.java

    override fun getDataSource() = DataSource.LOCAL
}

/**
 * Model loader to load [AppIcon]'s
 */
class AppIconModelLoader @Inject constructor(
    private val appIconFetcherProvider: Provider<AppIconFetcher>
) : ModelLoader<AppIcon, Drawable> {

    override fun buildLoadData(
        model: AppIcon,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Drawable> {
        return ModelLoader.LoadData<Drawable>(
            ObjectKey(model),
            appIconFetcherProvider.get().apply { app = model }
        )
    }

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