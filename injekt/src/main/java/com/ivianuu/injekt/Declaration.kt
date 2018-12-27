package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Represents a dependency declaration.
 */
data class Declaration<T : Any> private constructor(
    val primaryType: KClass<T>,
    val name: String?
) {

    var override = false
    var eager = false
    var secondaryTypes: List<KClass<*>> = emptyList()

    lateinit var kind: Kind
    lateinit var provider: DeclarationBuilder.(Parameters) -> T
    lateinit var instance: Instance<T>

    internal val classes: List<KClass<*>> get() = listOf(primaryType) + secondaryTypes

    /**
     * Add a compatible kind to current bounded definition
     */
    infix fun bind(type: KClass<*>) = apply {
        if (secondaryTypes.contains(type)) return@apply

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

    override fun toString(): String {
        val kind = kind.toString()
        val name = name?.let { "name:'$name', " } ?: ""
        val type = "type:'${primaryType.getFullName()}'"
        val secondaryTypes = if (secondaryTypes.isNotEmpty()) {
            val typesAsString = secondaryTypes.joinToString(", ") { it.getFullName() }
            ", secondary types:$typesAsString"
        } else ""
        return "$kind[$name$type$secondaryTypes]"
    }

    enum class Kind { FACTORY, SINGLE }

    companion object {

        fun <T : Any> create(
            primaryType: KClass<T>,
            name: String? = null,
            kind: Kind,
            provider: DeclarationBuilder.(Parameters) -> T
        ): Declaration<T> {
            val declaration = Declaration(primaryType, name)

            declaration.kind = kind

            declaration.instance = when (kind) {
                Kind.FACTORY -> FactoryInstance(declaration)
                Kind.SINGLE -> SingleInstance(declaration)
            }

            declaration.provider = provider

            return declaration
        }

    }
}