package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Kind
import kotlin.reflect.KClass

/**
 * A module consists of dependency declarations and describes how dependencies are provided.
 * Each module should represent a logical unit. For instance there should be separate modules for each functionality
 * of your application.
 */
@ModuleDslMarker
class Module internal constructor(val name: String? = null) {
    internal val declarations = mutableSetOf<Declaration<*>>()
}

/**
 * Defines a [Module] (with an optional name).
 */
fun module(name: String? = null, body: Module.() -> Unit) =
    Module(name).apply(body)

@DslMarker
annotation class ModuleDslMarker

/**
 * Provides a dependency
 */
inline fun <reified T : Any> Module.factory(
    name: String? = null,
    internal: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = factory(type = T::class, name = name, internal = internal, body = body)

/**
 * Provides a dependency
 */
fun <T : Any> Module.factory(
    type: KClass<T>,
    name: String? = null,
    internal: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
) = provide(type = type, kind = Kind.Factory, name = name, internal = internal, body = body)

/**
 * Provides a singleton dependency
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    createOnStart: Boolean = false,
    internal: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = single(
    type = T::class,
    name = name,
    createOnStart = createOnStart,
    internal = internal,
    body = body
)

/**
 * Provides a singleton dependency
 */
fun <T : Any> Module.single(
    type: KClass<T>,
    name: String? = null,
    createOnStart: Boolean = false,
    internal: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
) = provide(
    type = type,
    kind = Kind.Single(createOnStart),
    name = name,
    internal = internal,
    body = body
)

inline fun <reified T : Any> Module.provide(
    kind: Kind,
    name: String? = null,
    internal: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = provide(type = T::class, kind = kind, name = name, internal = internal, body = body)

fun <T : Any> Module.provide(
    type: KClass<T>,
    kind: Kind,
    name: String? = null,
    internal: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
): Declaration<T> {
    val declaration =
        Declaration(
            kind = kind,
            moduleName = name,
            primaryType = type,
            name = name,
            binding = { context, params -> body.invoke(DeclarationBuilder(context), params) },
            internal = internal
        )

    val existingDeclaration = declarations.firstOrNull { it.key == declaration.key }
    if (existingDeclaration != null) {
        throw OverrideException(declaration, existingDeclaration)
    }

    declarations.add(declaration)

    return declaration
}

class DeclarationBuilder(val context: ComponentContext)

/**
 * Provides a dependency which has already been declared in the current context (total set of modules of the
 * current component) to be able to inject transitive dependencies within a module.
 */
inline fun <reified T : Any> DeclarationBuilder.get(name: String? = null) = get(T::class, name)

/**
 * Provides a dependency which has already been declared in the current context (total set of modules of the
 * current component) to be able to inject transitive dependencies within a module.
 */
fun <T : Any> DeclarationBuilder.get(
    type: KClass<T>,
    name: String? = null
) = context.get(type, name)

/**
 * Lazy version of [get]
 */
inline fun <reified T : Any> DeclarationBuilder.lazy(
    name: String? = null,
    noinline params: (() -> Parameters)? = null
) = lazy(T::class, name, params)

/**
 * Lazy version of [get]
 */
fun <T : Any> DeclarationBuilder.lazy(
    type: KClass<T>,
    name: String? = null,
    params: (() -> Parameters)? = null
) = context.inject(type, name, params)