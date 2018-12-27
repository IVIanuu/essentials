package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Represents a dependency declaration.
 */
data class Declaration<T : Any>(
    val primaryType: KClass<T>,
    val name: String?,
    val kind: Kind,
    var secondaryTypes: List<KClass<*>> = emptyList(),
    var setBindings: List<SetBinding>,
    val provider: (Component, Parameters) -> T,
    val eager: Boolean
) {

    internal val classes: List<KClass<*>> get() = listOf(primaryType) + secondaryTypes

    val key = "Class: ${primaryType.java.name}${name?.let { " Name: $it" }.orEmpty()}"

    /**
     * Add a compatible kind to current bounded definition
     */
    infix fun bind(type: KClass<*>) = apply {
        if (!type.java.isAssignableFrom(this.primaryType.java)) {
            throw IllegalArgumentException("Can't bind kind '$type' for definition $this")
        } else {
            secondaryTypes += type
        }
    }

    /**
     * Add a compatible kind to current bounded definition
     */
    infix fun binds(types: Array<KClass<*>>) = apply {
        types.forEach { bind(it) }
    }

    fun <S : Any> bindIntoSet(implementation: KClass<S>, setName: String? = null) {
        setBindings += SetBinding(implementation, setName)
    }

    enum class Kind { FACTORY, SINGLE }
}

data class SetBinding(
    val implementation: KClass<*>,
    val setName: String? = null
)