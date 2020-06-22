package com.ivianuu.essentials.coil

import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.ivianuu.essentials.app.BindAppInitializer
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient

@BindAppInitializer
@Transient
class CoilAppInitializer(
    private val imageLoaderProvider: @Provider () -> ImageLoader
) {
    init {
        Coil.setImageLoader(object : ImageLoaderFactory {
            override fun newImageLoader(): ImageLoader = imageLoaderProvider()
        })
    }
}
