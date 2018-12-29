package com.ivianuu.essentials.injection

import com.ivianuu.injekt.*
import kotlin.reflect.KClass

fun lazyComponent(
    name: String? = null,
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition
) = lazy { component(name, createEagerInstances, definition) }

inline fun <reified T : Any> Module.lazy(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = lazy(T::class, name, params)

fun <T : Any> Module.lazy(
    type: KClass<T>,
    name: String? = null,
    params: ParamsDefinition? = null
): Lazy<T> = kotlin.lazy { get(type, name, params) }