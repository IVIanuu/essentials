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
    val moduleDeclarations = modules.map { it.declarations }.fold {
        info { "Registering declaration $it" }
    }

    return Component(moduleDeclarations, dependsOn)
}

/**
 * Injects requested dependency immediately.
 */
inline fun <reified T : Any> Component.get(
    name: String? = null,
    params: Parameters = emptyParameters()
) = get(T::class, name, params)

/**
 * Injects requested dependency lazily.
 */
inline fun <reified T : Any> Component.inject(
    name: String? = null,
    noinline params: (() -> Parameters)? = null
) = inject(T::class, name, params)

/**
 * Injects requested dependency lazily.
 */
fun <T : Any> Component.inject(
    type: KClass<T>,
    name: String? = null,
    params: (() -> Parameters)? = null
) = lazy {
    get(type, name, params?.invoke() ?: emptyParameters())
}

/**
 * Returns a provider for the requested dependency
 */
inline fun <reified T : Any> Component.provider(
    name: String? = null,
    params: Parameters = emptyParameters()
) = provider(T::class, name, params)

/**
 * Returns a provider for the requested dependency
 */
fun <T : Any> Component.provider(
    type: KClass<T>,
    name: String? = null,
    params: Parameters = emptyParameters()
) = { get(type, name, params) }

private fun Iterable<Set<Declaration<*>>>.fold(each: ((Declaration<*>) -> Unit)? = null): Set<Declaration<*>> =
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