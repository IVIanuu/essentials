package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Returns a [Component] which contains [modules]
 */
fun component(vararg modules: Module) =
    component(modules = modules.toList())

/**
 * Returns a [Component] which contains [modules]
 * And depends on any of [dependsOn]
 */
fun component(
    modules: Collection<Module> = emptyList(),
    dependsOn: Collection<Component> = emptyList()
): Component {
    val component = Component()

    modules.forEach { component.addModule(it) }
    dependsOn.forEach { component.addDependency(it) }

    return component
}

/**
 * Returns a instance of [T] matching the [name] and [params]
 */
inline fun <reified T : Any> Component.get(
    name: String? = null,
    noinline params: () -> Parameters = emptyParametersProvider
) = get(T::class, name, params)

/**
 * Lazily returns a instance of [T] matching the [name] and [params]
 */
inline fun <reified T : Any> Component.inject(
    name: String? = null,
    noinline params: () -> Parameters = emptyParametersProvider
) = inject(T::class, name, params)

/**
 * Lazily returns a instance of [T] matching the [type], [name] and [params]
 */
fun <T : Any> Component.inject(
    type: KClass<T>,
    name: String? = null,
    params: () -> Parameters = emptyParametersProvider
) = lazy { get(type, name, params) }