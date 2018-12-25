package com.ivianuu.injekt

import kotlin.reflect.KClass

/**
 * Helper trait for simplifying injection syntax.
 *
 * Can be applied to a class which holds a [Component].
 * The extension functions [inject], [get] and [canInject] can then be used instead of explicitly referencing
 * `component` every time.
 */
interface ComponentHolder {
    val component: Component
}

inline fun <reified T : Any> ComponentHolder.inject(
    name: String? = null, noinline parameters: (() -> Parameters)? = null
) = inject(T::class, name, parameters)

fun <T : Any> ComponentHolder.inject(
    clazz: KClass<T>,
    name: String? = null,
    parameters: (() -> Parameters)? = null
) =
    lazy(LazyThreadSafetyMode.NONE) {
        withComponent {
            it.get(clazz, name, parameters?.invoke() ?: emptyParameters())
        }
    }

inline fun <reified T : Any> ComponentHolder.get(name: String? = null) =
    get(T::class, name)

fun <T : Any> ComponentHolder.get(clazz: KClass<T>, name: String? = null) =
    withComponent { it.get<T>(clazz, name) }

inline fun <reified T : Any> ComponentHolder.canInject(name: String? = null) =
    canInject(T::class, name)

fun <T : Any> ComponentHolder.canInject(clazz: KClass<T>, name: String? = null) =
    withComponent { it.canInject(clazz, name) }

@Suppress("SENSELESS_COMPARISON")
fun <R> ComponentHolder.withComponent(body: (Component) -> R) =
// Despite the non-null type the `component` can be null if the above extension functions are used before the
// `component` property was initialized.
    if (component == null) {
        throw ComponentNotInitializedException("component is null! Make sure to use ComponentHolder's extension functions *after* component property was initialized.")
    } else {
        body.invoke(component)
    }
