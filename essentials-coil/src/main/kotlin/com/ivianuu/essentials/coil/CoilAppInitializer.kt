package com.ivianuu.essentials.coil

import coil.Coil
import coil.ImageLoader
import com.ivianuu.essentials.app.AppInitializerBinding
import com.ivianuu.injekt.FunBinding

@AppInitializerBinding
@FunBinding
fun initializeCoil(imageLoaderFactory: () -> ImageLoader) {
    Coil.setImageLoader(imageLoaderFactory)
}
