package com.ivianuu.essentials.util

import com.ivianuu.essentials.app.appContext
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module

fun esUtilModule() = module(name = "EsUtilModule") {
    factory { BroadcastFactory(appContext()) }
}