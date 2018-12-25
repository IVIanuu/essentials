package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Type
import com.ivianuu.injekt.Declaration.Type.FACTORY
import com.ivianuu.injekt.Declaration.Type.SINGLE
import kotlin.reflect.KClass

/**
 * A module consists of dependency declarations and describes how dependencies are provided.
 * Each module should represent a logical unit. For instance there should be separate modules for each functionality
 * of your application.
 */
@ModuleDslMarker
class Module internal constructor(val name: String? = null) {
    internal val declarations = mutableMapOf<Key, Declaration<*>>()
}

/**
 * Defines a [Module] (with an optional name).
 */
fun module(name: String? = null, body: Module.() -> Unit) =
    Module(name).apply(body)

@DslMarker
annotation class ModuleDslMarker

/**
 * Provides the dependency via a factory. A new instance will be created every time the dependency is requested.
 */
inline fun <reified T : Any> Module.factory(
    name: String? = null,
    internal: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = factory(T::class, name, internal, body)

/**
 * Provides the dependency via a factory. A new instance will be created every time the dependency is requested.
 */
fun <T : Any> Module.factory(
    clazz: KClass<T>,
    name: String? = null,
    internal: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
) = provide(clazz, FACTORY, name, internal, false, body)

/**
 * Provides the dependency as a singleton. Only one instance (per component) will be created.
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    eager: Boolean = false,
    internal: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = single(T::class, name, eager, internal, body)

/**
 * Provides the dependency as a singleton. Only one instance (per component) will be created.
 */
fun <T : Any> Module.single(
    clazz: KClass<T>,
    name: String? = null,
    eager: Boolean = false,
    internal: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
) = provide(clazz, SINGLE, name, internal, eager, body)

inline fun <reified T : Any> Module.provide(
    type: Type,
    name: String? = null,
    internal: Boolean = false,
    eager: Boolean = false,
    noinline body: DeclarationBuilder.(Parameters) -> T
) = provide(T::class, type, name, internal, eager, body)

fun <T : Any> Module.provide(
    clazz: KClass<T>,
    type: Type,
    name: String? = null,
    internal: Boolean = false,
    eager: Boolean = false,
    body: DeclarationBuilder.(Parameters) -> T
): Declaration<T> {
    val key = Key.of(clazz, name)
    val declaration =
        Declaration(
            key = key,
            type = type,
            moduleName = name,
            clazz = clazz,
            name = name,
            provider = { context, params -> body.invoke(DeclarationBuilder(context), params) },
            internal = internal,
            eager = eager
        )

    val existingDeclaration = declarations[key]
    if (existingDeclaration != null) {
        throw OverrideException(declaration, existingDeclaration)
    }

    declarations[key] = declaration

    return declaration
}

class DeclarationBuilder(val context: ComponentContext)

/**
 * Provides a dependency which has already been declared in the current context (total set of modules of the
 * current component) to be able to inject transitive dependencies within a module.
 */
inline fun <reified T : Any> DeclarationBuilder.get(name: String? = null) =
    context.get<T>(name = name, internal = true)

/**
 * Provides a [Lazy] version of dependency. Should only be required to circumvent a circular dependency cycle.
 * Better solution is to structure classes in a way that circular dependencies are not necessary.
 */
inline fun <reified T : Any> DeclarationBuilder.lazy(name: String? = null) =
    context.inject<T>(name = name, internal = true)
