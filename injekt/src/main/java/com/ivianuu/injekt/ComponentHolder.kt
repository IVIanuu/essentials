package com.ivianuu.injekt

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
) = lazy { component.get<T>(name, params) }

/** Calls trough [Component.get] */
inline fun <reified T : Any> ComponentHolder.get(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = component.get(T::class, name, params)