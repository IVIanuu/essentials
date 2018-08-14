package com.ivianuu.essentials.app

import android.app.Application
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.ivianuu.essentials.util.AppIcon
import com.ivianuu.essentials.util.AppIconModelLoader
import javax.inject.Inject

/**
 * Initializes glide
 */
class GlideAppInitializer @Inject constructor(
        private val appIconLoaderFactory: AppIconModelLoader.Factory
) : AppInitializer {

    override fun init(app: Application) {
        Glide.get(app).registry
            .append(AppIcon::class.java, Bitmap::class.java, appIconLoaderFactory)
    }

}