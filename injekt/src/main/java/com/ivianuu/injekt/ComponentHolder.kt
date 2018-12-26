package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Helper for shorter syntax
 */
interface ComponentHolder {
    /**
     * The [Component] of this class
     */
    val component: Component
}

/** Calls trough [Component.inject] */
inline fun <reified T : Any> ComponentHolder.inject(
    name: String? = null, noinline params: (() -> Parameters)? = null
) = inject(T::class, name, params)

/** Calls trough [Component.inject] */
fun <T : Any> ComponentHolder.inject(
    type: KClass<T>,
    name: String? = null,
    params: (() -> Parameters)? = null
) =
    lazy { component.get(type, name, params?.invoke() ?: emptyParameters()) }

/** Calls trough [Component.get] */
inline fun <reified T : Any> ComponentHolder.get(name: String? = null) =
    get(T::class, name)

/** Calls trough [Component.get] */
fun <T : Any> ComponentHolder.get(type: KClass<T>, name: String? = null) =
    component.get(type, name)

/** Calls trough [Component.provider] */
inline fun <reified T : Any> ComponentHolder.provider(name: String? = null) =
    provider(T::class, name)

/** Calls trough [Component.provider] */
fun <T : Any> ComponentHolder.provider(type: KClass<T>, name: String? = null) =
    component.provider(type, name)
