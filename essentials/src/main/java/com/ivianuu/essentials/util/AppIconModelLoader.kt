package com.ivianuu.essentials.util

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import javax.inject.Inject

data class AppIcon(val packageName: String)

/**
 * Fetches images for [AppIcon]'s
 */
class AppIconFetcher(
        private val packageManager: PackageManager,
        private val app: AppIcon,
        private val width: Int,
        private val height: Int
) : DataFetcher<Bitmap> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        val drawable = packageManager.getApplicationIcon(app.packageName)
        if (drawable != null) {
            callback.onDataReady(drawable.toBitmap(width, height))
        } else {
            callback.onLoadFailed(Exception())
        }
    }

    override fun cleanup() {
    }

    override fun cancel() {
    }

    override fun getDataClass() = Bitmap::class.java

    override fun getDataSource() = DataSource.LOCAL
}

/**
 * Model loader to load [AppIcon]'s
 */
class AppIconModelLoader(private val packageManager: PackageManager) : ModelLoader<AppIcon, Bitmap> {

    override fun buildLoadData(
            model: AppIcon,
            width: Int,
            height: Int,
            options: Options
    ): ModelLoader.LoadData<Bitmap> {
        return ModelLoader.LoadData<Bitmap>(
                ObjectKey(model),
                AppIconFetcher(packageManager, model, width, height)
        )
    }

    override fun handles(model: AppIcon) = true

    class Factory @Inject constructor(
            private val packageManager: PackageManager
    ) : ModelLoaderFactory<AppIcon, Bitmap> {

        override fun build(multiFactory: MultiModelLoaderFactory) =
                AppIconModelLoader(packageManager)

        override fun teardown() {
        }
    }
}