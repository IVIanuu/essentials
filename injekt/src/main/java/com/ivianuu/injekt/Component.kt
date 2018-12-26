package com.ivianuu.injekt

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
    val moduleDeclarations = modules.map { it.declarations }.fold {
        info { "Registering declaration $it" }
    }

    return Component(moduleDeclarations, dependsOn)
}

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
    dependsOn: Iterable<Component>
) {

    val context = ComponentContext(this, dependsOn)

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
            .forEach { it.get(context, emptyParameters()) }
    }

    /**
     * Injects requested dependency lazily.
     */
    inline fun <reified T : Any> inject(
        name: String? = null,
        noinline params: (() -> Parameters)? = null
    ) = context.inject<T>(name, params)

    /**
     * Injects requested dependency lazily.
     */
    fun <T : Any> inject(
        type: KClass<T>,
        name: String? = null,
        params: (() -> Parameters)? = null
    ) = context.inject(type, name, params)

    /**
     * Injects requested dependency immediately.
     */
    inline fun <reified T : Any> get(
        name: String? = null,
        params: Parameters = emptyParameters()
    ) = get(T::class, name, params)

    /**
     * Injects requested dependency immediately.
     */
    fun <T : Any> get(
        type: KClass<T>,
        name: String? = null,
        params: Parameters = emptyParameters()
    ) = context.get(type, name, params)

    /**
     * Returns a provider for the requested dependency
     */
    inline fun <reified T : Any> provider(
        name: String? = null,
        params: Parameters = emptyParameters(),
        internal: Boolean = false
    ) = provider(T::class, name, params, internal)

    /**
     * Returns a provider for the requested dependency
     */
    fun <T : Any> provider(
        type: KClass<T>,
        name: String? = null,
        params: Parameters = emptyParameters(),
        internal: Boolean = false
    ) = context.provider(type, name, params, internal)

    internal fun <T : Any> thisComponentInject(
        type: KClass<T>,
        name: String?,
        params: Parameters,
        internal: Boolean
    ): T? {
        val instance = instances.firstOrNull {
            (if (!internal) !it.declaration.internal else true)
                    && it.declaration.classes.contains(type)
                    && name == it.declaration.name
        }
        return if (instance == null || (instance.declaration.internal && !internal)) {
            null
        } else {
            try {
                info { "Injecting dependency for ${instance.declaration.key}" }
                instance.get(context, params) as T
            } catch (e: InjektException) {
                throw e
            } catch (e: Exception) {
                throw InstanceCreationException("Could not instantiate $instance", e)
            }
        }
    }

    internal fun canInject(type: KClass<*>, name: String? = null) =
        context.canInject(type, name)

    internal fun thisComponentCanInject(
        type: KClass<*>,
        name: String?,
        internal: Boolean
    ) =
        instances.any {
            (if (!internal) !it.declaration.internal else true)
                    && it.declaration.classes.contains(type)
                    && name == it.declaration.name
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

    inline fun <reified T : Any> inject(
        name: String? = null,
        noinline params: (() -> Parameters)? = null,
        internal: Boolean = false
    ) =
        inject(T::class, name, params, internal)

    fun <T : Any> inject(
        type: KClass<T>,
        name: String? = null,
        params: (() -> Parameters)? = null,
        internal: Boolean = false
    ) = lazy {
        get(
            type,
            name = name,
            params = params?.invoke() ?: emptyParameters(),
            internal = internal
        )
    }

    inline fun <reified T : Any> get(
        name: String? = null,
        params: Parameters = emptyParameters(),
        internal: Boolean = false
    ) =
        get(T::class, name, params, internal)

    fun <T : Any> get(
        type: KClass<T>,
        name: String? = null,
        params: Parameters = emptyParameters(),
        internal: Boolean = false
    ) = injectInternal(type, name, params, internal)

    inline fun <reified T : Any> provider(
        name: String? = null,
        params: Parameters = emptyParameters(),
        internal: Boolean = false
    ) = provider(T::class, name, params, internal)

    fun <T : Any> provider(
        type: KClass<T>,
        name: String? = null,
        params: Parameters = emptyParameters(),
        internal: Boolean = false
    ) = { get(type, name, params, internal) }

    private fun <T : Any> injectInternal(
        type: KClass<T>,
        name: String? = null,
        params: Parameters = emptyParameters(),
        internal: Boolean = false
    ): T {
        val value = thisComponent.thisComponentInject(type, name, params, internal)
        return when {
            value != null -> value
            else -> {
                val component = dependsOn.find { component -> component.canInject(type, name) }
                    ?: throw InjectionException("No binding found for ${type.java.name + name.orEmpty()}")
                component.get(type, name)
            }
        }
    }

    internal fun canInject(
        type: KClass<*>,
        name: String? = null,
        internal: Boolean = false
    ): Boolean =
        when {
            thisComponent.thisComponentCanInject(type, name, internal = internal) -> true
            else -> dependsOn.any { it.canInject(type, name) }
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