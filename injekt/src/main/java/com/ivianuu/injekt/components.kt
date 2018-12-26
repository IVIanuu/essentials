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
    modules: List<Module> = emptyList(),
    dependsOn: List<Component> = emptyList()
): Component {
    val moduleDeclarations = modules
        .flatMap { module -> module.declarations + module.subModules.flatMap { it.declarations } }

    // find overrides
    moduleDeclarations
        .groupBy { it.key }
        .filterValues { it.size > 1 }
        .map { it.value.first() to it.value[1] }
        .forEach { throw OverrideException(it.first, it.second) }

    moduleDeclarations.forEach { info { "Registering declaration $it" } }

    return Component(moduleDeclarations, dependsOn)
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