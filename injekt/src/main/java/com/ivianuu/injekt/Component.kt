package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Along with [Modules][Module] a Component is the heart of dependency injection via InjektPlugins.
 *
 * It performs the actual injection and will also hold the singleton instances of dependencies declared as singletons.
 * This is an important aspect of InjektPlugins! As long as the same Component reference is used for injection, the same
 * singleton instances are reused. Once the Component is eligible for garbage collection so are the instances hold by
 * this component. The developer is responsible for holding a Component reference and releasing it when necessary. This
 * design was chosen in contrast to other DI libraries that for instance work with a global, singleton state to prevent
 * accidental memory leaks.
 *
 * However great care should be taken when dependent Components are specified via `dependsOn` at [component]. If
 * dependent Components are specified and the current Component has a greater scope (lifespan) than the other Components,
 * references to these Components are still held. Dependent Components should always have an equal or greater scope
 * than the current Component.
 */
class Component internal constructor(
    declarations: Set<Declaration<*>>,
    private val dependsOn: Iterable<Component>
) {

    private val instances = mutableSetOf<InstanceHolder<*>>()

    init {
        declarations
            .map {
                when (it.kind) {
                    is Declaration.Kind.Factory -> FactoryInstanceHolder(it)
                    is Declaration.Kind.Single -> SingleInstanceHolder(it)
                }
            }
            .forEach { instances.add(it) }

        // Initialize eager singletons
        instances
            .filter {
                (it.declaration.kind as? Declaration.Kind.Single)?.eager == true
            }
            .forEach { it.get(this, emptyParameters()) }
    }

    /**
     * Returns a instance of [T] for the given parameters
     */
    fun <T : Any> get(
        type: KClass<T>,
        name: String? = null,
        params: Parameters = emptyParameters()
    ) = getInternal(type, name, params)

    private fun <T : Any> getInternal(
        type: KClass<T>,
        name: String? = null,
        params: Parameters = emptyParameters()
    ): T {
        val value = thisComponentInject(type, name, params)
        return when {
            value != null -> value
            else -> {
                val component = dependsOn.find { component -> component.canInject(type, name) }
                    ?: throw InjectionException("No binding found for ${type.java.name + name.orEmpty()}")
                component.getInternal(type, name)
            }
        }
    }

    private fun canInject(
        type: KClass<*>,
        name: String? = null
    ): Boolean =
        when {
            thisComponentCanInject(type, name) -> true
            else -> dependsOn.any { it.canInject(type, name) }
        }

    private fun <T : Any> thisComponentInject(
        type: KClass<T>,
        name: String?,
        params: Parameters
    ): T? {
        val instance = instances.firstOrNull {
            it.declaration.classes.contains(type)
                    && name == it.declaration.name
        }
        return if (instance == null) {
            null
        } else {
            try {
                info { "Injecting dependency for ${instance.declaration.key}" }
                @Suppress("UNCHECKED_CAST")
                instance.get(this, params) as T
            } catch (e: InjektException) {
                throw e
            } catch (e: Exception) {
                throw InstanceCreationException("Could not instantiate $instance", e)
            }
        }
    }

    private fun thisComponentCanInject(
        type: KClass<*>,
        name: String?
    ) =
        instances.any {
            it.declaration.classes.contains(type)
                    && name == it.declaration.name
        }
}