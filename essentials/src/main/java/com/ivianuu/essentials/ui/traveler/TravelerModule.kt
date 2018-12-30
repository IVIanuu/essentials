package com.ivianuu.essentials.ui.traveler

import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.traveler.Router

/**
 * Provides the global [Router]
 */
val travelerModule = module(name = "TravelerModule") {
    single { Router() }
}