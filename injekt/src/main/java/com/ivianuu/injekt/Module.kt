package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Kind
import kotlin.reflect.KClass

/**
 * A module provides the actual dependencies
 */
class Module internal constructor(
    val createOnStart: Boolean,
    val override: Boolean
) {
    internal var declarations = mutableListOf<Declaration<*>>()
    internal val subModules = mutableListOf<Module>()

    /**
     * Adds the [declaration]
     */
    fun <T : Any> declare(
        declaration: Declaration<T>
    ): Declaration<T> {
        val createOnStart = if (createOnStart) createOnStart else declaration.eager
        val override = if (override) override else declaration.override

        declaration.eager = createOnStart
        declaration.override = override

        declarations.add(declaration)

        return declaration
    }

    /**
     * Adds the [module] to [Module.subModules]
     */
    fun module(module: Module) {
        subModules.add(module)
    }
}

/**
 * Defines a [Module]
 */
fun module(
    createOnStart: Boolean = false,
    override: Boolean = false,
    body: Module.() -> Unit
) = Module(createOnStart, override).apply(body)

/**
 * Provides a dependency
 */
inline fun <reified T : Any> Module.factory(
    name: String? = null,
    override: Boolean = false,
    noinline provider: DeclarationBuilder.(Parameters) -> T
) = factory(type = T::class, name = name, override = override, provider = provider)

/**
 * Provides a dependency
 */
fun <T : Any> Module.factory(
    type: KClass<T>,
    name: String? = null,
    override: Boolean = false,
    provider: DeclarationBuilder.(Parameters) -> T
) = declare(
    type = type,
    kind = Kind.FACTORY,
    name = name,
    createOnStart = false,
    override = override,
    provider = provider
)

/**
 * Provides a singleton dependency
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    override: Boolean = false,
    createOnStart: Boolean = false,
    noinline provider: DeclarationBuilder.(Parameters) -> T
) = single(
    type = T::class,
    name = name,
    override = override,
    createOnStart = createOnStart,
    provider = provider
)

/**
 * Provides a singleton dependency
 */
fun <T : Any> Module.single(
    type: KClass<T>,
    name: String? = null,
    override: Boolean = false,
    createOnStart: Boolean = false,
    provider: DeclarationBuilder.(Parameters) -> T
) = declare(
    type = type,
    kind = Kind.SINGLE,
    name = name,
    override = override,
    createOnStart = createOnStart,
    provider = provider
)

/**
 * Adds a [Declaration] for the provided params
 */
inline fun <reified T : Any> Module.declare(
    kind: Kind,
    name: String? = null,
    override: Boolean = false,
    createOnStart: Boolean = false,
    noinline provider: DeclarationBuilder.(Parameters) -> T
) = declare(
    type = T::class,
    kind = kind,
    name = name,
    override = override,
    createOnStart = createOnStart,
    provider = provider
)

/**
 * Adds a [Declaration] for the provided params
 */
fun <T : Any> Module.declare(
    type: KClass<T>,
    kind: Kind,
    name: String? = null,
    override: Boolean = false,
    createOnStart: Boolean = false,
    provider: DeclarationBuilder.(Parameters) -> T
) = declare(
    Declaration.create(type, name, kind, provider).also {
        it.eager = createOnStart
        it.override = override
    }
)

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
fun Module.module(
    createOnStart: Boolean = false,
    override: Boolean = false,
    body: Module.() -> Unit
) =
    Module(createOnStart, override).apply(body).also { module(it) }