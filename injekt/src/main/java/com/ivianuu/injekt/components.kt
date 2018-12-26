package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Defines a [Component].
 */
fun component(vararg modules: Module) =
    component(modules = modules.toList())

/**
 * Defines a [Component].
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

private inline fun <T, K> Iterable<T>.findDuplicates(selector: (T) -> K): List<T> {
    val set = HashSet<K>()
    val list = ArrayList<T>()
    for (e in this) {
        val key = selector(e)
        if (!set.add(key)) list.add(e)
    }
    return list
}

private fun Iterable<List<Declaration<*>>>.fold(each: ((Declaration<*>) -> Unit)? = null): Set<Declaration<*>> =
    fold(mutableSetOf()) { acc, currDeclarations ->
        currDeclarations.forEach { entry ->
            val existingDeclaration = acc.firstOrNull { it.key == entry.key }

            existingDeclaration?.let { declaration ->
                throw OverrideException(entry, declaration)
            }
            each?.invoke(entry)
        }
        acc.apply { addAll(currDeclarations) }
    }