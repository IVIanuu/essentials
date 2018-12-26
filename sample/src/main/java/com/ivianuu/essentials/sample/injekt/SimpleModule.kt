package com.ivianuu.essentials.sample.injekt

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import kotlin.reflect.KClass

inline fun <reified T : Any> simpleModule(
    instance: T,
    name: String? = null,
    createOnStart: Boolean = false,
    noinline body: (Module.() -> Unit)? = null
) = simpleModule(T::class, instance, name, createOnStart, body)

fun <T : Any> simpleModule(
    clazz: KClass<T>,
    instance: T,
    name: String? = null,
    createOnStart: Boolean = false,
    body: (Module.() -> Unit)? = null
) = module(name, createOnStart) {
    // bind instance
    single(clazz) { instance }

    body?.invoke(this)
}