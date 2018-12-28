package com.ivianuu.essentials.injection

import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module

inline fun <reified T : Any> bindInstanceModule(
    instance: T,
    name: String? = null
) = module(name = name) {
    factory { instance }
}