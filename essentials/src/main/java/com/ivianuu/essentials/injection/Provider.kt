package com.ivianuu.essentials.injection

import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentHolder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ParamsDefinition
import kotlin.reflect.KClass

interface Provider<T> {
    fun get(params: ParamsDefinition? = null): T
}

inline fun <reified T : Any> Component.provider(
    name: String? = null,
    noinline defaultParams: ParamsDefinition? = null
) = provider(T::class, name, defaultParams)

fun <T : Any> Component.provider(
    type: KClass<T>,
    name: String? = null,
    defaultParams: ParamsDefinition? = null
) = object : Provider<T> {
    override fun get(params: ParamsDefinition?) =
        get(type, name, params ?: defaultParams)
}

inline fun <reified T : Any> ComponentHolder.provider(
    name: String? = null,
    noinline defaultParams: ParamsDefinition? = null
) = provider(T::class, name, defaultParams)

fun <T : Any> ComponentHolder.provider(
    type: KClass<T>,
    name: String? = null,
    defaultParams: ParamsDefinition? = null
) = component.provider(type, name, defaultParams)

inline fun <reified T : Any> Module.provider(
    name: String? = null,
    noinline defaultParams: ParamsDefinition? = null
) = provider(T::class, name, defaultParams)

fun <T : Any> Module.provider(
    type: KClass<T>,
    name: String? = null,
    defaultParams: ParamsDefinition? = null
) = component.provider(type, name, defaultParams)