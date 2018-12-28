package com.ivianuu.essentials.injection

import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module
import kotlin.reflect.KClass

fun <T : Any> bindInstanceModule(
    instance: T,
    override: Boolean = false,
    name: String? = null
) = module(override = override, name = name) {
    factory(instance::class as KClass<T>) { instance }
}