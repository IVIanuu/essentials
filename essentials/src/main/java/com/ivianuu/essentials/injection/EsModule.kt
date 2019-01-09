package com.ivianuu.essentials.injection

import com.ivianuu.essentials.app.esAppInitializersModule
import com.ivianuu.essentials.app.esAppServicesModule
import com.ivianuu.essentials.ui.traveler.travelerModule
import com.ivianuu.injekt.module

/**
 * Core modules
 */
val esModule = module("EsModule") {
    module(esAppInitializersModule)
    module(esAppServicesModule)
    module(systemServiceModule)
    module(travelerModule)
}