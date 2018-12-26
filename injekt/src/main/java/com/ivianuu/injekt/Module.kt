package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Kind
import kotlin.reflect.KClass

/**
 * A module consists of dependency declarations and describes how dependencies are provided.
 * Each module should represent a logical unit. For instance there should be separate modules for each functionality
 * of your application.
 */
@ModuleDslMarker
class Module internal constructor(
    val name: String? = null,
    val createOnStart: Boolean = false
) {
    internal val declarations = mutableSetOf<Declaration<*>>()
}

/**
 * Defines a [Module] (with an optional name).
 */
fun module(
    name: String? = null,
    createOnStart: Boolean = false,
    body: Module.() -> Unit
) =
    Module(name, createOnStart).apply(body)

@DslMarker
annotation class ModuleDslMarker

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
) = provide(type = type, kind = Kind.Factory, name = name, body = body)

/**
 * Provides a singleton dependency
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    createOnStart: Boolean = this.createOnStart,
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
    createOnStart: Boolean = this.createOnStart,
    body: DeclarationBuilder.(Parameters) -> T
) = provide(
    type = type,
    kind = Kind.Single(createOnStart),
    name = name,
    body = body
)

/**
 * Provides a dependency
 */
inline fun <reified T : Any> Module.provide(
    kind: Kind,
    name: String? = null,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = provide(type = T::class, kind = kind, name = name, body = body)

/**
 * Provides a dependency
 */
fun <T : Any> Module.provide(
    type: KClass<T>,
    kind: Kind,
    name: String? = null,
    body: DeclarationBuilder.(Parameters) -> T
): Declaration<T> {
    val declaration =
        Declaration(
            kind = kind,
            moduleName = name,
            primaryType = type,
            name = name,
            binding = { context, params ->
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
    noinline parameters: () -> Parameters = { emptyParameters() }
) = get(T::class, name, parameters)

/** Calls trough [Component.get] */
fun <T : Any> DeclarationBuilder.get(
    type: KClass<T>,
    name: String? = null,
    parameters: () -> Parameters = { emptyParameters() }
) = component.get(type, name, parameters())