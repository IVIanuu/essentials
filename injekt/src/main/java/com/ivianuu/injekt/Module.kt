package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Kind
import kotlin.reflect.KClass

/**
 * A module provides the actual dependencies
 */
class Module internal constructor(
    val name: String? = null
) {
    val declarations = mutableListOf<Declaration<*>>()
    val subModules = mutableListOf<Module>()
}

/**
 * Defines a [Module]
 */
fun module(
    name: String? = null,
    body: Module.() -> Unit
) = Module(name).apply(body)

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
) = provide(type = type, kind = Kind.FACTORY, name = name, createOnStart = false, body = body)

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
) = provide(
    type = type,
    kind = Kind.SINGLE,
    name = name,
    createOnStart = createOnStart,
    body = body
)

/**
 * Provides a dependency
 */
inline fun <reified T : Any> Module.provide(
    kind: Kind,
    name: String? = null,
    createOnStart: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = provide(type = T::class, kind = kind, name = name, createOnStart = createOnStart, body = body)

/**
 * Provides a dependency
 */
fun <T : Any> Module.provide(
    type: KClass<T>,
    kind: Kind,
    name: String? = null,
    createOnStart: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
): Declaration<T> {
    val declaration =
        Declaration(
            kind = kind,
            primaryType = type,
            name = name,
            eager = createOnStart,
            provider = { context, params ->
                body.invoke(DeclarationBuilder(context), params)
            }
        )

    val existingDeclaration = declarations.firstOrNull { it.key == declaration.key }
    if (existingDeclaration != null) {
        throw OverrideException(declaration, existingDeclaration)
    }

    declarations.add(declaration)

    return declaration
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
fun Module.module(
    name: String? = null,
    body: Module.() -> Unit
) = Module(name).apply(body)
    .also { module(it) }

/**
 * Adds the [module] to [Module.subModules]
 */
fun Module.module(module: Module) {
    subModules.add(module)
}