package com.ivianuu.essentials.ui.core

import androidx.compose.runtime.CompositionReferenceAccessor
import androidx.compose.runtime.Ambient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionReference

@Composable
val <T : Any> Ambient<T>.currentOrNull: T?
    get() {
        val ref = compositionReference()
        val ambients = CompositionReferenceAccessor.getAmbientScope(ref)
        return ambients[this as Ambient<Any?>]?.value as? T
    }

@Composable
val Ambient<*>.hasCurrentValue: Boolean get() {
    val ref = compositionReference()
    return this in CompositionReferenceAccessor.getAmbientScope(ref)
}

@Composable
inline fun <T : Any> Ambient<T>.currentOrElse(defaultValue: () -> T): T =
    currentOrNull ?: defaultValue()
