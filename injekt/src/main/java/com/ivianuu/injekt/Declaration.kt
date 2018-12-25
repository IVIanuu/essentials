package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Represents a dependency declaration.
 */
data class Declaration<T : Any>(
    val type: Type,
    val moduleName: String?,
    val clazz: KClass<T>,
    var types: List<KClass<*>> = emptyList(),
    val name: String?,
    val provider: Provider<T>,
    val internal: Boolean
) {

    internal val classes: List<KClass<*>> = listOf(clazz) + types

    val key = clazz.java.name + name.orEmpty()

    /**
     * Add a compatible type to current bounded definition
     */
    infix fun bind(clazz: KClass<*>): Declaration<*> {
        if (!clazz.java.isAssignableFrom(this.clazz.java)) {
            throw IllegalArgumentException("Can't bind type '$clazz' for definition $this")
        } else {
            types += clazz
        }
        return this
    }

    sealed class Type {
        object Factory : Type()

        data class Single(
            val eager: Boolean,
            val synchronized: Boolean
        ) : Type()
    }

    override fun toString() =
        "${clazz.java.name}(type=$type${name?.let { ", name=$it" }.orEmpty()}${moduleName?.let { ", module=$it" }.orEmpty()})"
}
