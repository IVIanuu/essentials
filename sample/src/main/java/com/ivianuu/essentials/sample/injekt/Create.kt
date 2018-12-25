package com.ivianuu.essentials.sample.injekt

/*
/**
 * Provides the dependency via a factory. A new instance will be created every time the dependency is requested.
 */
inline fun <reified T : Any> Module.factory(
    name: String? = null,
    internal: Boolean = false
) = factory(name, internal) { create<T>() }

/**
 * Provides the dependency via a factory. A new instance will be created every time the dependency is requested.
 */
fun <T : Any> Module.factory(
    clazz: KClass<T>,
    name: String? = null,
    internal: Boolean = false
) = factory(clazz, name, internal) { create(clazz) }

/**
 * Create a Single definition for given type T
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    eager: Boolean = false,
    internal: Boolean = false
) = single(name, eager, internal) { create<T>() }

/**
 * Create a Single definition for given type T
 */
fun <T : Any> Module.single(
    clazz: KClass<T>,
    eager: Boolean = false,
    name: String? = null,
    internal: Boolean = false
) = single(clazz, name, eager, internal) { create(clazz) }

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
inline fun <reified T : Any> DeclarationBuilder.create() = create(T::class)

/**
 * Create instance for type T and inject dependencies into 1st constructor
 */
fun <T : Any> DeclarationBuilder.create(clazz: KClass<T>): T {
    val ctor = clazz.java.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    val args = ctor.parameterTypes.map { get(it) }.toTypedArray()
    return ctor.newInstance(*args) as T
}*/