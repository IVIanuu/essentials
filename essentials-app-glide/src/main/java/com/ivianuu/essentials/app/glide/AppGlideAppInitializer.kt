package com.ivianuu.essentials.app.glide

import android.app.Application
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.ivianuu.essentials.app.AppInitializer

/**
 * Initializes glide
 */
class AppGlideAppInitializer(private val factory: AppIconModelLoader.Factory) : AppInitializer {

    override fun initialize(app: Application) {
        Glide.get(app).registry
            .append(
                AppIcon::class.java,
                Drawable::class.java,
                factory
            )
    }

}