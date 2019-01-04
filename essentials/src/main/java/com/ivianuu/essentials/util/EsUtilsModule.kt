package com.ivianuu.essentials.util

import com.ivianuu.injekt.android.applicationContext
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module

val esUtilsModule = module("EsUtilsModule") {
    factory { BroadcastFactory(applicationContext()) }
}