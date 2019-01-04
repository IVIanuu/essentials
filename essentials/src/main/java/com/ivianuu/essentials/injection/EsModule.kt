package com.ivianuu.essentials.injection

import com.ivianuu.essentials.app.esAppInitializersModule
import com.ivianuu.essentials.app.esAppServicesModule
import com.ivianuu.essentials.ui.traveler.travelerModule
import com.ivianuu.essentials.util.esUtilsModule
import com.ivianuu.injekt.module

/**
 * Core modules
 */
val esModule = module {
    module(esAppInitializersModule)
    module(esAppServicesModule)
    module(esUtilsModule)
    module(systemServiceModule)
    module(travelerModule)
}