package com.ivianuu.essentials.sample.injekt

import com.ivianuu.injekt.*
import kotlin.reflect.KClass

/**
 * Provides the dependency via a factory. A new instance will be created every time the dependency is requested.
 */
inline fun <reified T : Any> Module.factory(
    name: String? = null
) = factory(name) { create<T>() }

/**
 * Provides the dependency via a factory. A new instance will be created every time the dependency is requested.
 */
fun <T : Any> Module.factory(
    clazz: KClass<T>,
    name: String? = null
) = factory(clazz, name) { create(clazz) }

/**
 * Create a Single definition for given kind T
 */
inline fun <reified T : Any> Module.single(
    name: String? = null,
    createOnStart: Boolean = false
) = single(name, createOnStart) { create<T>() }

/**
 * Create a Single definition for given kind T
 */
fun <T : Any> Module.single(
    clazz: KClass<T>,
    name: String? = null,
    createOnStart: Boolean = false
) = single(clazz, name, createOnStart) { create(clazz) }

/**
 * Create instance for kind T and inject dependencies into 1st constructor
 */
inline fun <reified T : Any> DeclarationBuilder.create() = create(T::class)

/**
 * Create instance for kind T and inject dependencies into 1st constructor
 */
fun <T : Any> DeclarationBuilder.create(clazz: KClass<T>): T {
    val ctor = clazz.java.constructors.firstOrNull() ?: error("No constructor found for class '$clazz'")
    val args = ctor.parameterTypes.map { get(it.kotlin) }.toTypedArray()
    return ctor.newInstance(*args) as T
}