package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Holds a [Component] and allows for shorter syntax
 */
interface ComponentHolder {
    /**
     * The [Component] of this class
     */
    val component: Component
}

/** Calls trough [Component.inject] */
inline fun <reified T : Any> ComponentHolder.inject(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = inject(T::class, name, params)

/** Calls trough [Component.inject] */
fun <T : Any> ComponentHolder.inject(
    type: KClass<T>,
    name: String? = null,
    params: ParamsDefinition? = null
) = lazy { component.get(type, name, params) }

/** Calls trough [Component.get] */
inline fun <reified T : Any> ComponentHolder.get(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = get(T::class, name, params)

/** Calls trough [Component.get] */
fun <T : Any> ComponentHolder.get(
    type: KClass<T>,
    name: String? = null,
    params: ParamsDefinition? = null
) = component.get(type, name, params)