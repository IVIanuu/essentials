package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Represents a dependency declaration.
 */
data class Declaration<T : Any>(
    val key: Key,
    val type: Type,
    val moduleName: String?,
    val clazz: KClass<T>,
    var types: List<KClass<*>> = emptyList(),
    val name: String?,
    val provider: Provider<T>,
    val internal: Boolean,
    val eager: Boolean
) {

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

    enum class Type { FACTORY, SINGLE }

    override fun toString() =
        "${clazz.java.name}(type=$type${name?.let { ", name=$it" }.orEmpty()}${moduleName?.let { ", module=$it" }.orEmpty()})"
}
