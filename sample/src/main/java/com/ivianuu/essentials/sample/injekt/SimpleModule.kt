package com.ivianuu.essentials.sample.injekt

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import kotlin.reflect.KClass

inline fun <reified T : Any> simpleModule(
    instance: T,
    name: String? = null,
    noinline body: (Module.() -> Unit)? = null
) = simpleModule(T::class, instance, name, body)

fun <T : Any> simpleModule(
    clazz: KClass<T>,
    instance: T,
    name: String? = null,
    body: (Module.() -> Unit)? = null
) = module(name) {
    // bind instance
    single(clazz) { instance }

    body?.invoke(this)
}