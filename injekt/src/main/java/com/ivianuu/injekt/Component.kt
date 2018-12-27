package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * The actual dependency container which provides declarations
 */
class Component internal constructor() {

    private val declarations = mutableListOf<Declaration<*>>()
    private val declarationsByName = mutableMapOf<String, Declaration<*>>()
    private val declarationsByType = mutableMapOf<KClass<*>, Declaration<*>>()

    private val dependencies = mutableListOf<Component>()

    /**
     * Returns a instance of [T] matching the [type], [name] and [params]
     */
    fun <T : Any> get(
        type: KClass<T>,
        name: String? = null,
        params: () -> Parameters = emptyParametersProvider
    ) = getInternal(type, name, params)

    /**
     * Adds the [Declaration]s of the [Module]
     */
    fun addModule(module: Module) {
        (listOf(module) + module.subModules)
            .flatMap { it.declarations }
            .forEach { addDeclaration(it) }
    }

    /**
     * Adds the [component] as a dependency
     */
    fun addDependency(component: Component) {
        dependencies.add(component)
    }

    private fun addDeclaration(declaration: Declaration<*>) {
        val isOverride = declarations.remove(declaration)

        if (isOverride && !declaration.override) {
            throw OverrideException("Try to override declaration $declaration")
        }

        info {
            val kw = if (isOverride) "Override" else "Declare"
            "$kw ${declaration.key}"
        }

        declaration.instance.component = this

        declarations.add(declaration)

        if (declaration.eager) {
            declaration.instance.get(emptyParameters())
        }
    }

    private fun <T : Any> getInternal(
        type: KClass<T>,
        name: String?,
        params: () -> Parameters
    ): T = synchronized(this) {
        val declaration = findDeclaration(type, name)
        return if (declaration != null) {
            @Suppress("UNCHECKED_CAST")
            declaration.instance.create(params()) as T
        } else {
            throw InjectionException("Could not find declaration for ${type.java.name + name.orEmpty()}")
        }
    }

    private fun findDeclaration(
        type: KClass<*>,
        name: String?
    ): Declaration<*>? {
        var declaration = if (name != null) {
            declarationsByName[name]
                ?: declarations.firstOrNull { it.name == name }
                    ?.also { declarationsByName[name] = it }
        } else {
            declarationsByType[type]
                ?: declarations.firstOrNull { it.classes.contains(type) }
                    ?.also { declarationsByType[type] = it }
        }

        // search in dependencies
        if (declaration == null) {
            for (component in dependencies) {
                declaration = component.findDeclaration(type, name)
                if (declaration != null) {
                    if (name != null) {
                        declarationsByName[name] = declaration
                    } else {
                        declarationsByType[type] = declaration
                    }
                    break
                }
            }
        }

        return declaration
    }

}