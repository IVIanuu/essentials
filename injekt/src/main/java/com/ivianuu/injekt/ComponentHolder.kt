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
    name: String? = null, noinline params: (() -> Parameters)? = null
) = inject(T::class, name, params)

fun <T : Any> ComponentHolder.inject(
    type: KClass<T>,
    name: String? = null,
    params: (() -> Parameters)? = null
) =
    lazy(LazyThreadSafetyMode.NONE) {
        withComponent {
            it.get(type, name, params?.invoke() ?: emptyParameters())
        }
    }

inline fun <reified T : Any> ComponentHolder.get(name: String? = null) =
    get(T::class, name)

fun <T : Any> ComponentHolder.get(type: KClass<T>, name: String? = null) =
    withComponent { it.get(type, name) }

inline fun <reified T : Any> ComponentHolder.provider(name: String? = null) =
    provider(T::class, name)

fun <T : Any> ComponentHolder.provider(type: KClass<T>, name: String? = null) =
    withComponent { it.provider(type, name) }

@Suppress("SENSELESS_COMPARISON")
fun <R> ComponentHolder.withComponent(body: (Component) -> R) =
// Despite the non-null kind the `component` can be null if the above extension functions are used before the
// `component` property was initialized.
    if (component == null) {
        throw ComponentNotInitializedException("component is null! Make sure to use ComponentHolder's extension functions *after* component property was initialized.")
    } else {
        body.invoke(component)
    }
