package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * The actual dependency container which provides instances
 */
class Component internal constructor(
    declarations: List<Declaration<*>>,
    private val dependsOn: Iterable<Component>
) {

    private val instances = mutableListOf<InstanceHolder<*>>()

    init {
        // map the declarations to instance holders
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
        val instance = instances.firstOrNull {
            it.declaration.classes.contains(type)
                    && it.declaration.name == name
        }

        return if (instance != null) {
            try {
                info { "Injecting instance for ${instance.declaration.key}" }
                @Suppress("UNCHECKED_CAST")
                instance.get(this, params()) as T
            } catch (e: InjektException) {
                throw e
            } catch (e: Exception) {
                throw InstanceCreationException("Could not instantiate $instance", e)
            }
        } else {
            val component = dependsOn.find {
                it.instances.any {
                    it.declaration.classes.contains(type)
                            && name == it.declaration.name
                }
            }
                ?: throw InjectionException("Could not create instance for ${type.java.name + name.orEmpty()}")

            component.getInternal(type, name, params)
        }
    }
}