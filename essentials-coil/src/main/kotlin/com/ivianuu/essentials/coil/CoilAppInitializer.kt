package com.ivianuu.essentials.coil

import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.ivianuu.essentials.app.GivenAppInitializer
import com.ivianuu.injekt.given

@GivenAppInitializer
fun initializeCoil() {
    Coil.setImageLoader(object : ImageLoaderFactory {
        override fun newImageLoader(): ImageLoader = given()
    })
}
