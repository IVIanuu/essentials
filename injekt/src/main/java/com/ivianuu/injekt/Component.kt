package com.ivianuu.injekt

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * The actual dependency container which provides declarations
 */
class Component internal constructor(
    declarations: List<Declaration<*>>,
    private val dependencies: Iterable<Component>
) {

    private val declarations = mutableSetOf<InstanceHolder<*>>()
    private val declarationsByName: MutableMap<String, InstanceHolder<*>> = ConcurrentHashMap()
    private val declarationsByType: MutableMap<KClass<*>, InstanceHolder<*>> = ConcurrentHashMap()

    init {
        // map the declarations to instance holders
        declarations
            .map {
                when (it.kind) {
                    Declaration.Kind.FACTORY -> FactoryInstanceHolder(it)
                    Declaration.Kind.SINGLE -> SingleInstanceHolder(it)
                }
            }
            .forEach { this.declarations.add(it) }

        // Initialize eager singletons
        this.declarations
            .filter { it.declaration.kind == Declaration.Kind.SINGLE && it.declaration.eager }
            .forEach { it.get(this, emptyParameters()) }
    }

    /**
     * Returns a instance of [T] matching the [type], [name] and [params]
     */
    fun <T : Any> get(
        type: KClass<T>,
        name: String? = null,
        params: () -> Parameters = emptyParametersProvider
    ) = getInternal(type, name, params)

    private fun <T : Any> getInternal(
        type: KClass<T>,
        name: String?,
        params: () -> Parameters
    ): T = synchronized(this) {
        val instance = findDeclaration(type, name)
        return if (instance != null) {
            @Suppress("UNCHECKED_CAST")
            instance.create(this, params()) as T
        } else {
            throw InjectionException("Could not find declaration for ${type.java.name + name.orEmpty()}")
        }
    }

    private fun findDeclaration(type: KClass<*>, name: String?): InstanceHolder<*>? {
        var declaration: InstanceHolder<*>?

        if (name != null) {
            declaration = declarationsByName[name]
                    ?: declarations.firstOrNull { it.declaration.name == name }
                ?.also { declarationsByName[name] = it }
        } else {
            declaration = declarationsByType[type]
                    ?: declarations.firstOrNull { it.declaration.classes.contains(type) }
                ?.also { declarationsByType[type] = it }
        }

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