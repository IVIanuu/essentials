package com.ivianuu.injekt

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
    dependencies: Collection<Component> = emptyList(),
    name: String? = null
) = Component(name).apply {
    dependencies.forEach { addDependency(it) }
    modules.forEach { addModule(it) }
}

/**
 * Returns a instance of [T] matching the [name] and [params]
 */
inline fun <reified T : Any> Component.get(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = get(T::class, name, params)

/**
 * Lazily returns a instance of [T] matching the [name] and [params]
 */
inline fun <reified T : Any> Component.inject(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = lazy { get<T>(name, params) }

inline fun <reified K : Any, reified T : Any> Component.getMap(
    name: String? = null,
    noinline params: ParamsDefinition? = null
) = getMap(K::class, T::class, name, params)