package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * The actual dependency container which provides declarations
 */
class Component internal constructor(
    private val dependencies: Set<Component>,
    modules: Collection<Module>
) {

    private val declarations = mutableListOf<Declaration<*>>()
    private val declarationsByName = mutableMapOf<String, Declaration<*>>()
    private val declarationsByType = mutableMapOf<KClass<*>, Declaration<*>>()

    init {
        modules
            .flatMap { it.declarations }
            .forEach { declaration ->
                val isOverride = declarations.remove(declaration)
                        || dependencies.any {
                    it.thisComponentFindDeclaration(
                        declaration.primaryType,
                        declaration.name
                    ) != null
                }

                if (isOverride && !declaration.options.override) {
                    throw OverrideException("Try to override declaration $declaration")
                }

                info {
                    val kw = if (isOverride) "Override" else "Declare"
                    "$kw $declaration"
                }

                declaration.instance.component = this

                declarations.add(declaration)

                if (declaration.options.createOnStart) {
                    declaration.instance.get(null)
                }
            }
    }

    /**
     * Returns a instance of [T] matching the [type], [name] and [params]
     */
    fun <T : Any> get(
        type: KClass<T>,
        name: String? = null,
        params: ParamsDefinition? = null
    ) = getInternal(type, name, params)

    fun <T : Any> getSet(
        type: KClass<T>,
        name: String? = null,
        params: ParamsDefinition? = null
    ): Set<T> {
        val declarations = (listOf(this) + dependencies)
            .flatMap { it.getSetBindings(type, name) }

        return declarations.map { it.resolveInstance(params) }
            .toSet() as Set<T>
    }

    fun <T : Any> getLazySet(
        type: KClass<T>,
        name: String? = null,
        params: ParamsDefinition? = null
    ): Set<() -> T> {
        val declarations = (listOf(this) + dependencies)
            .flatMap { it.getSetBindings(type, name) }

        return declarations.map { { it.resolveInstance(params) } }
            .toSet() as Set<() -> T>
    }

    fun <K : Any, T : Any> getMap(
        keyType: KClass<K>,
        type: KClass<T>,
        name: String? = null,
        params: ParamsDefinition? = null
    ): Map<K, T> {
        val declarations = (listOf(this) + dependencies)
            .flatMap { it.getMapBindings(type, keyType, name) }

        return declarations.map {
            val binding = it.mapBindings.first { it.keyType == keyType }
            binding.key to it.resolveInstance(params)
        }.toMap() as Map<K, T>
    }

    private fun <T : Any> getInternal(
        type: KClass<T>,
        name: String?,
        params: ParamsDefinition?
    ): T = synchronized(this) {
        val declaration = findDeclaration(type, name)

        return if (declaration != null) {
            @Suppress("UNCHECKED_CAST")
            declaration.resolveInstance(params) as T
        } else {
            throw InjectionException("Could not find declaration for ${type.java.name + name.orEmpty()}")
        }
    }

    private fun findDeclaration(
        type: KClass<*>,
        name: String?
    ) = thisComponentFindDeclaration(type, name) ?: findDeclarationInDependencies(type, name)

    private fun thisComponentFindDeclaration(
        type: KClass<*>,
        name: String?
    ): Declaration<*>? {
        return if (name != null) {
            declarationsByName[name]
                ?: declarations.firstOrNull { it.name == name }
                    ?.also { declarationsByName[name] = it }
        } else {
            declarationsByType[type]
                ?: declarations.firstOrNull { it.classes.contains(type) }
                    ?.also { declarationsByType[type] = it }
        }
    }

    private fun findDeclarationInDependencies(
        type: KClass<*>,
        name: String?
    ): Declaration<*>? {
        for (component in dependencies) {
            val declaration = component.findDeclaration(type, name)
            if (declaration != null) {
                if (name != null) {
                    declarationsByName[name] = declaration
                } else {
                    declarationsByType[type] = declaration
                }

                return declaration
            }
        }

        return null
    }

    private fun getSetBindings(
        type: KClass<*>,
        name: String?
    ) = declarations
        .filter { declaration ->
            declaration.setBindings
                .any { it.type == type && it.name == name }
        }

    private fun getMapBindings(
        type: KClass<*>,
        keyType: KClass<*>,
        name: String?
    ) = declarations
        .filter { declaration ->
            declaration.mapBindings
                .any {
                    it.type == type
                            && it.keyType == keyType
                            && it.name == name
                }
        }
}