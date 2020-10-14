package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.staticAmbientOf
import kotlinx.coroutines.DisposableHandle

@Stable
class RetainedObjects {
    private val backing = mutableMapOf<Any, Any?>()

    operator fun <T> get(key: Any): T = backing[key] as T

    fun <T> getOrNull(key: Any): T? = backing[key] as? T

    operator fun <T> set(key: Any, value: T) {
        remove(key)
        backing[key] = value
    }

    operator fun contains(key: Any): Boolean = backing.containsKey(key)

    fun remove(key: Any) {
        val value = backing.remove(key)
        (value as? DisposableHandle)?.dispose()
    }

    fun dispose() {
        backing.keys.toList().forEach { remove(it) }
    }
}

val RetainedObjectsAmbient = staticAmbientOf<RetainedObjects>() {
    error("No RetainedObjects specified")
}

// todo remove once compose is fixed
@Composable
inline fun <T> rememberRetained(
    key: Any = currentComposer.currentCompoundKeyHash,
    noinline init: () -> T
): T = rememberRetained(inputs = *emptyArray(), key = key, init = init)

@Composable
inline fun <T> rememberRetained(
    vararg inputs: Any?,
    key: Any = currentComposer.currentCompoundKeyHash,
    noinline init: () -> T
): T {
    val retainedObjects = RetainedObjectsAmbient.current
    var value: ValueWithInputs<T>? = retainedObjects.getOrNull(key)
    if (value != null && !value.inputs.contentEquals(inputs)) {
        value = null
    }

    if (value == null) {
        value = ValueWithInputs(init(), inputs)
        retainedObjects[key] = value
    }

    return value.value
}

@PublishedApi
internal class ValueWithInputs<T>(
    val value: T,
    val inputs: Array<out Any?>
)
