package com.ivianuu.essentials.ui.effect

import kotlin.reflect.KProperty

interface EffectContext {
    fun <T> cache(key: Any, inputs: Array<out Any?>?, block: () -> T): T

    fun onDispose(callback: () -> Unit)

    fun dispose()
}

fun EffectContext(): EffectContext = EffectContextImpl()

inline fun <T> EffectContext.memo(
    key: Any = sourceLocation(),
    vararg inputs: Any?,
    crossinline calculation: () -> T
): T = cache(key, inputs) { calculation() }

private class EffectContextImpl : EffectContext {

    private class EffectState(
        val inputs: Array<out Any?>?,
        val value: Any?
    )

    private val effectStates = mutableMapOf<Any, EffectState>()

    private val onDisposeCallbacks = mutableListOf<() -> Unit>()

    override fun <T> cache(key: Any, inputs: Array<out Any?>?, block: () -> T): T {
        val state = effectStates[key]

        return if (state != null && ((inputs != null && state.inputs != null && inputs.contentEquals(
                state.inputs
            )) || (inputs == null && state.inputs == null))
        ) {
            state.value as T
        } else {
            val newValue = block()
            effectStates[key] = EffectState(inputs, newValue)
            newValue
        }
    }

    override fun onDispose(callback: () -> Unit) {
        onDisposeCallbacks.add(callback)
    }

    override fun dispose() {
        effectStates.clear()
        onDisposeCallbacks.toList().forEach { it() }
    }
}

inline fun EffectContext.onActive(
    key: Any = sourceLocation(),
    crossinline callback: () -> Unit
) {
    memo(key) { callback() }
}

class State<T> @PublishedApi internal constructor(var value: T) {
    operator fun component1(): T = value

    operator fun component2(): (T) -> Unit = { value = it }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }
}

inline fun <T> EffectContext.state(
    key: Any = sourceLocation(),
    vararg inputs: Any?,
    crossinline init: () -> T
) = memo(key = key, inputs = *inputs, calculation = { State(init()) })

inline fun sourceLocation(): String {
    val element = Throwable().stackTrace.first()
    return "${element.className}:${element.methodName}:${element.lineNumber}"
}