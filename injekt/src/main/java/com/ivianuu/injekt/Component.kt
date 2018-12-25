package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Type.FACTORY
import com.ivianuu.injekt.Declaration.Type.SINGLE
import kotlin.reflect.KClass

/**
 * Defines a [Component].
 */
fun component(vararg modules: Module) =
    component(modules = modules.asIterable())

/**
 * Defines a [Component].
 */
fun component(
    modules: Iterable<Module> = emptyList(),
    dependsOn: Iterable<Component> = emptyList()
): Component {
    val moduleDeclarations = modules.map { module -> module.declarations }.fold {
        info { "Registering declaration $it" }
    }

    return Component(moduleDeclarations, dependsOn)
}

/**
 * Along with [Modules][Module] a Component is the heart of dependency injection via Injekt.
 *
 * It performs the actual injection and will also hold the singleton instances of dependencies declared as singletons.
 * This is an important aspect of Injekt! As long as the same Component reference is used for injection, the same
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
    private val declarations: Set<Declaration<*>>,
    dependsOn: Iterable<Component>
) {

    private val instances = mutableMapOf<String, Any>()
    val context = ComponentContext(this, dependsOn)

    init {
        // Initialize eager singletons
        declarations
            .filter { it.eager }
            .forEach { declaration ->
                instances[declaration.key] = declaration.provider(context, emptyParameters()) as Any
            }
    }

    /**
     * Returns `true` if this component is capable of injecting requested dependency.
     */
    inline fun <reified T : Any> canInject(name: String? = null) =
        canInject(T::class, name)

    /**
     * Returns `true` if this component is capable of injecting requested dependency.
     */
    fun <T : Any> canInject(clazz: KClass<T>, name: String? = null) =
        context.canInject(clazz, name)

    /**
     * Injects requested dependency lazily.
     */
    inline fun <reified T : Any> inject(
        name: String? = null,
        noinline parameters: (() -> Parameters)? = null
    ) = context.inject<T>(name, parameters)

    /**
     * Injects requested dependency lazily.
     */
    fun <T : Any> inject(
        clazz: KClass<T>,
        name: String? = null,
        parameters: (() -> Parameters)? = null
    ) = context.inject(clazz, name, parameters)

    /**
     * Injects requested dependency immediately.
     */
    inline fun <reified T : Any> get(
        name: String? = null,
        parameters: Parameters = emptyParameters()
    ) = get(T::class, name, parameters)

    /**
     * Injects requested dependency immediately.
     */
    fun <T : Any> get(
        clazz: KClass<T>,
        name: String? = null,
        parameters: Parameters = emptyParameters()
    ) = context.get(clazz, name, parameters)

    internal fun <T : Any> thisComponentInject(
        clazz: KClass<T>,
        name: String?,
        parameters: Parameters,
        internal: Boolean
    ): T? {
        val declaration = declarations.firstOrNull {
            (if (!internal) !it.internal else true)
                    && it.classes.contains(clazz)
                    && name == it.name
        }
        if (declaration == null || (declaration.internal && !internal)) {
            return null
        } else {
            try {
                info { "Injecting dependency for ${declaration.key}" }
                val instance = when (declaration.type) {
                    FACTORY -> declaration.provider(context, parameters).also {
                        debug { "Created instance for ${declaration.key}" }
                    }
                    SINGLE -> {
                        instances[declaration.key]?.also {
                            debug { "Returning existing singleton instance for ${declaration.key}" }
                        } ?: declaration.provider(context, parameters).also { newInstance ->
                            debug { "Created singleton instance for ${declaration.key}" }
                            instances[declaration.key] = newInstance
                        }
                    }
                }
                return instance as T
            } catch (e: InjektException) {
                throw e
            } catch (e: Exception) {
                throw InstanceCreationException("Could not instantiate $declaration", e)
            }
        }
    }

    internal fun thisComponentCanInject(
        clazz: KClass<*>,
        name: String?,
        internal: Boolean
    ) =
        declarations.any {
            (if (!internal) !it.internal else true)
                    && it.classes.contains(clazz)
                    && name == it.name
        }

}

/**
 * ComponentContext is used internally to represent the total set of [Components][Component] and thus possible
 * injections for the current context consisting of the current Component and all Components that have been specified
 * via `dependsOn` (see [component]).
 */
class ComponentContext(
    private val thisComponent: Component,
    private val dependsOn: Iterable<Component>
) {

    inline fun <reified T : Any> canInject(name: String? = null, internal: Boolean = false) =
        canInject(T::class, name, internal)

    fun <T : Any> canInject(
        clazz: KClass<T>,
        name: String? = null,
        internal: Boolean = false
    ): Boolean =
        when {
            thisComponent.thisComponentCanInject(clazz, name, internal = internal) -> true
            else -> dependsOn.any { it.canInject(clazz, name) }
        }

    inline fun <reified T : Any> inject(
        name: String? = null,
        noinline parameters: (() -> Parameters)? = null,
        internal: Boolean = false
    ) =
        inject(T::class, name, parameters, internal)

    fun <T : Any> inject(
        clazz: KClass<T>,
        name: String? = null,
        parameters: (() -> Parameters)? = null,
        internal: Boolean = false
    ) = lazy {
        get(
            clazz,
            name = name,
            parameters = parameters?.invoke() ?: emptyParameters(),
            internal = internal
        )
    }

    inline fun <reified T : Any> get(
        name: String? = null,
        parameters: Parameters = emptyParameters(),
        internal: Boolean = false
    ) =
        get(T::class, name, parameters, internal)

    fun <T : Any> get(
        clazz: KClass<T>,
        name: String? = null,
        parameters: Parameters = emptyParameters(),
        internal: Boolean = false
    ) = injectInternal(clazz, name, parameters, internal)

    private fun <T : Any> injectInternal(
        clazz: KClass<T>,
        name: String? = null,
        parameters: Parameters = emptyParameters(),
        internal: Boolean = false
    ): T {
        val value = thisComponent.thisComponentInject(clazz, name, parameters, internal)
        return when {
            value != null -> value
            else -> {
                val component = dependsOn.find { component -> component.canInject(clazz, name) }
                    ?: throw InjectionException("No binding found for ${clazz.java.name + name.orEmpty()}")
                component.get(clazz, name)
            }
        }
    }
}

private fun Iterable<Set<Declaration<*>>>.fold(each: ((Declaration<*>) -> Unit)? = null): Set<Declaration<*>> =
    fold(mutableSetOf()) { acc, currDeclarations ->
        currDeclarations.forEach { entry ->
            val existingDeclaration = acc.firstOrNull { it.key == entry.key }

            existingDeclaration?.let { declaration ->
                if (!declaration.internal) throw OverrideException(entry, declaration)
            }
            each?.invoke(entry)
        }
        acc.apply { addAll(currDeclarations) }
    }