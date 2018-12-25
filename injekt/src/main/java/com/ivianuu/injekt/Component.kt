package com.ivianuu.injekt

import com.ivianuu.injekt.Declaration.Type.FACTORY
import com.ivianuu.injekt.Declaration.Type.SINGLE
import com.ivianuu.injekt.Key.ClassKey
import com.ivianuu.injekt.Key.NameKey
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
    private val declarations: Declarations,
    dependsOn: Iterable<Component>
) {

    private val instances = mutableMapOf<Key, Any>()
    val context = ComponentContext(this, dependsOn)

    init {
        // Initialize eager singletons
        declarations.values
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
    ) =
        context.inject<T>(name, parameters)

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
    ) =
        context.get(clazz, name, parameters)

    @Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
    internal fun <T> thisComponentInjectByKey(
        key: Key,
        parameters: Parameters,
        internal: Boolean
    ): T? {
        val declaration = declarations[key]
        if (declaration == null || (declaration.internal && !internal)) {
            return null
        } else {
            try {
                info { "Injecting dependency for ${key.stringIdentifier}" }
                val instance = when (declaration.type) {
                    FACTORY -> declaration.provider(context, parameters).also {
                        debug { "Created instance for ${key.stringIdentifier}" }
                    }
                    SINGLE -> {
                        instances[key]?.also {
                            debug { "Returning existing singleton instance for ${key.stringIdentifier}" }
                        } ?: declaration.provider(context, parameters).also { newInstance ->
                            debug { "Created singleton instance for ${key.stringIdentifier}" }
                            instances[key] = newInstance as Any
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

    internal fun thisComponentCanInject(key: Key, internal: Boolean) =
        declarations.filter { if (!internal) !it.value.internal else true }.containsKey(key)

    internal fun canInject(key: Key) =
        context.canInject(key)

    internal fun <T> injectByKey(key: Key) =
        context.injectByKey<T>(key)
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

    fun <T> injectByKey(
        key: Key,
        parameters: Parameters = emptyParameters(),
        internal: Boolean = false
    ): T {
        val value = thisComponent.thisComponentInjectByKey<T>(key, parameters, internal)
        return when {
            value != null -> value
            else -> {
                val component = dependsOn.find { component -> component.canInject(key) }
                    ?: throw InjectionException("No binding found for ${key.stringIdentifier}")
                component.injectByKey(key) as T
            }
        }
    }

    fun canInject(key: Key, internal: Boolean = false): Boolean =
        when {
            thisComponent.thisComponentCanInject(key = key, internal = internal) -> true
            else -> dependsOn.any { component -> component.canInject(key) }
        }

    inline fun <reified T : Any> canInject(name: String? = null, internal: Boolean = false) =
        canInject(T::class, name, internal)

    fun <T : Any> canInject(clazz: KClass<T>, name: String? = null, internal: Boolean = false) =
        canInject(key = Key.of(clazz, name), internal = internal)

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
    ) =
        injectByKey<T>(key = Key.of(clazz, name), parameters = parameters, internal = internal)

}

private fun Iterable<Declarations>.fold(each: ((Declaration<*>) -> Unit)? = null): Declarations =
    fold(mutableMapOf()) { acc, currDeclarations ->
        currDeclarations.entries.forEach { entry ->
            val existingDeclaration = acc[entry.key]
            existingDeclaration?.let { declaration ->
                if (!declaration.internal) throw OverrideException(entry.value, declaration)
            }
            each?.invoke(entry.value)
        }
        acc.apply { putAll(currDeclarations) }
    }

private val Key.stringIdentifier
    get() = when (this) {
        is ClassKey<*> -> "class ${clazz.java.name}"
        is NameKey -> "name $name"
    }