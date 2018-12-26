package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Represents a dependency declaration.
 */
data class Declaration<T : Any>(
    val kind: Kind,
    val moduleName: String?,
    val primaryType: KClass<T>,
    var boundTypes: List<KClass<*>> = emptyList(),
    val name: String?,
    val provider: (Component, Parameters) -> T
) {

    internal val classes: List<KClass<*>> = listOf(primaryType) + boundTypes

    val key = "Class: ${primaryType.java.name}${name?.let { " Name: $it" }.orEmpty()}"

    /**
     * Add a compatible kind to current bounded definition
     */
    infix fun bind(type: KClass<*>): Declaration<*> {
        if (!type.java.isAssignableFrom(this.primaryType.java)) {
            throw IllegalArgumentException("Can't bind kind '$type' for definition $this")
        } else {
            boundTypes += type
        }
        return this
    }

    sealed class Kind {
        object Factory : Kind() {
            override fun toString() = "Factory"
        }

        data class Single(val eager: Boolean) : Kind()
    }

}
