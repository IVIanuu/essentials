package com.ivianuu.essentials.util

import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module

val esUtilsModule = module("EsUtilsModule") {
    factory { BroadcastFactory(get()) }
}