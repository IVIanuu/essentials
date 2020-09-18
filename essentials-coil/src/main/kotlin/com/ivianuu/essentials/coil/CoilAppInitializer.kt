package com.ivianuu.essentials.coil

import coil.Coil
import com.ivianuu.essentials.app.GivenAppInitializer
import com.ivianuu.injekt.given

@GivenAppInitializer
fun initializeCoil() {
    Coil.setImageLoader { given() }
}
