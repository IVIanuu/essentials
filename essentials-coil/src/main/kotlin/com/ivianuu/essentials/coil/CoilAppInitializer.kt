package com.ivianuu.essentials.coil

import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.BindAppInitializer
import com.ivianuu.injekt.Provider

@BindAppInitializer
class CoilAppInitializer(
    private val imageLoaderProvider: @Provider () -> ImageLoader
) : AppInitializer {
    init {
        Coil.setImageLoader(object : ImageLoaderFactory {
            override fun newImageLoader(): ImageLoader {
                return imageLoaderProvider()
            }
        })
    }
}