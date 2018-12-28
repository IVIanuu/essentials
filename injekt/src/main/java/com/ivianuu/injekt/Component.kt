package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * The actual dependency container which provides declarations
 */
class Component internal constructor() {

    private val declarations = mutableListOf<Declaration<*>>()
    private val declarationsByName = mutableMapOf<String, Declaration<*>>()
    private val declarationsByType = mutableMapOf<KClass<*>, Declaration<*>>()

    /**
     * Adds all [Declaration]s of the [module]
     */
    fun addModule(module: Module) {
        module.declarations.forEach { saveDeclaration(it, false) }
    }

    /**
     * Adds all
     */
    fun addDependency(dependency: Component) {
        dependency.declarations.forEach { saveDeclaration(it, true) }
    }

    private fun saveDeclaration(
        declaration: Declaration<*>,
        fromDependency: Boolean
    ) {
        val isOverride = declarations.remove(declaration)

        if (isOverride && !declaration.options.override) {
            throw OverrideException("Try to override declaration $declaration")
        }

        info {
            val kw = if (isOverride) "Override" else "Declare"
            "$kw $declaration"
        }

        if (!fromDependency) {
            declaration.instance.component = this
        }

        declarations.add(declaration)

        if (declaration.name != null) {
            declarationsByName[declaration.name] = declaration
        } else {
            declarationsByType[declaration.primaryType] = declaration
        }

        if (!fromDependency && declaration.options.createOnStart) {
            declaration.instance.get(null)
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
        val declarations = getSetBindings(type, name)

        return declarations.map { it.resolveInstance(params) }
            .toSet() as Set<T>
    }

    fun <T : Any> getLazySet(
        type: KClass<T>,
        name: String? = null,
        params: ParamsDefinition? = null
    ): Set<() -> T> {
        val declarations = getSetBindings(type, name)
        return declarations.map { { it.resolveInstance(params) } }
            .toSet() as Set<() -> T>
    }

    fun <K : Any, T : Any> getMap(
        keyType: KClass<K>,
        type: KClass<T>,
        name: String? = null,
        params: ParamsDefinition? = null
    ): Map<K, T> {
        val declarations = getMapBindings(type, keyType, name)
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
    ): Declaration<*>? = if (name != null) {
        declarationsByName[name]
    } else {
        declarationsByType[type]
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