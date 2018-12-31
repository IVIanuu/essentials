package com.ivianuu.essentials.injection

import com.ivianuu.essentials.app.autoModule
import com.ivianuu.essentials.app.esAppInitializersModule
import com.ivianuu.essentials.app.esAppServicesModule
import com.ivianuu.injekt.module

/**
 * Core modules
 */
val esModule = module {
    module(autoModule)
    module(esAppInitializersModule)
    module(esAppServicesModule)
    module(systemServiceModule)
}