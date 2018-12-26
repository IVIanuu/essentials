package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Defines a [Component].
 */
fun component(vararg modules: Module) =
    component(modules = modules.asIterable())

/**
 * Defines a [Component].
 */
fun component(
    modules: Iterable<Module> = emptyList(),
    dependsOn: Iterable<Component> = emptyList()
): Component {
    val moduleDeclarations = modules
        .map { module ->
            module.declarations + module.subModules.flatMap { subModule ->
                subModule.declarations
            }
        }
        .fold { info { "Registering declaration $it" } }

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