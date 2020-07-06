package com.ivianuu.essentials.coil

import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.get

@AppInitializer
@Reader
fun initializeCoil() {
    Coil.setImageLoader(object : ImageLoaderFactory {
        override fun newImageLoader(): ImageLoader = get()
    })
}
