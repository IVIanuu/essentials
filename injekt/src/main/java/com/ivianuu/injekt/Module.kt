package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Kind
import kotlin.reflect.KClass

/**
 * A module provides the actual dependencies
 */
class Module internal constructor() {
    val declarations = mutableListOf<Declaration<*>>()
    val subModules = mutableListOf<Module>()
}

/**
 * Defines a [Module]
 */
fun module(
    body: Module.() -> Unit
) = Module().apply(body)

/**
 * Provides a dependency
 */
inline fun <reified T : Any> Module.factory(
    name: String? = null,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = factory(type = T::class, name = name, body = body)

/**
 * Provides a dependency
 */
fun <T : Any> Module.factory(
    type: KClass<T>,
    name: String? = null,
    body: DeclarationBuilder.(Parameters) -> T
) = declare(type = type, kind = Kind.FACTORY, name = name, createOnStart = false, body = body)

/**
 * Provides a singleton dependency
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    createOnStart: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = single(
    type = T::class,
    name = name,
    createOnStart = createOnStart,
    body = body
)

/**
 * Provides a singleton dependency
 */
fun <T : Any> Module.single(
    type: KClass<T>,
    name: String? = null,
    createOnStart: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
) = declare(
    type = type,
    kind = Kind.SINGLE,
    name = name,
    createOnStart = createOnStart,
    body = body
)

/**
 * Adds a [Declaration] for the provided params
 */
inline fun <reified T : Any> Module.declare(
    kind: Kind,
    name: String? = null,
    createOnStart: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = declare(type = T::class, kind = kind, name = name, createOnStart = createOnStart, body = body)

/**
 * Adds a [Declaration] for the provided params
 */
fun <T : Any> Module.declare(
    type: KClass<T>,
    kind: Kind,
    name: String? = null,
    createOnStart: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
) = declare(
    Declaration(
        kind = kind,
        primaryType = type,
        name = name,
        eager = createOnStart,
        provider = { context, params ->
            body.invoke(DeclarationBuilder(context), params)
        }
    )
)

/**
 * Adds the [declaration]
 */
fun <T : Any> Module.declare(
    declaration: Declaration<T>
) = declaration.also { declarations.add(it) }

/**
 * Adds a binding for [type] and [name] to [to] to a previously added [Declaration]
 */
inline fun <reified T : S, reified S : Any> Module.bind(name: String? = null) =
    bind(T::class, S::class, name)

/**
 * Adds a binding for [type] and [name] to [to] to a previously added [Declaration]
 */
fun <T : S, S : Any> Module.bind(
    type: KClass<T>,
    to: KClass<S>,
    name: String? = null
) {
    val declaration = declarations.firstOrNull { it.classes.contains(type) && name == name }
        ?: throw IllegalArgumentException("no declaration found for $type")

    declaration.bind(to)
}

class DeclarationBuilder(val component: Component)

/** Calls trough [Component.get] */
inline fun <reified T : Any> DeclarationBuilder.get(
    name: String? = null,
    noinline parameters: () -> Parameters = emptyParametersProvider
) = get(T::class, name, parameters)

/** Calls trough [Component.get] */
fun <T : Any> DeclarationBuilder.get(
    type: KClass<T>,
    name: String? = null,
    parameters: () -> Parameters = emptyParametersProvider
) = component.get(type, name, parameters)

/**
 * Defines a sub module
 */
fun Module.module(body: Module.() -> Unit) =
    Module().apply(body).also { module(it) }

/**
 * Adds the [module] to [Module.subModules]
 */
fun Module.module(module: Module) {
    subModules.add(module)
}