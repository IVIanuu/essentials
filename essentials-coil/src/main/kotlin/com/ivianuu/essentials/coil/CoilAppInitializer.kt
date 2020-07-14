package com.ivianuu.essentials.coil

import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.AppInitializers
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given

@AppInitializer
@Reader
fun initializeCoil() {
    Coil.setImageLoader(object : ImageLoaderFactory {
        override fun newImageLoader(): ImageLoader = given()
    })
}
