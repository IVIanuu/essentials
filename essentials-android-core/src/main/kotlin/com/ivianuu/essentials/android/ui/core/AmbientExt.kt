package com.ivianuu.essentials.android.ui.core

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.compositionReference

@Composable
val <T : Any> Ambient<T>.currentOrNull: T?
    get() {
        val ref = compositionReference()
        val ambients = ref.getAmbientScope()
        return ambients[this as Ambient<Any?>]?.value as? T
    }

@Composable
val Ambient<*>.hasCurrentValue: Boolean get() {
    val ref = compositionReference()
    return this in ref.getAmbientScope()
}

@Composable
val <T : Any> Ambient<T>.currentOrThrow: T
    get() = currentOrNull ?: error("No value provided for $this")

@Composable
inline fun <T : Any> Ambient<T>.currentOrElse(defaultValue: @Composable () -> T): T =
    currentOrNull ?: defaultValue()
