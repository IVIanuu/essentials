package com.ivianuu.essentials.ui.core

import androidx.compose.Ambient
import androidx.compose.AmbientAccessor
import androidx.compose.compositionReference

val <T : Any> Ambient<T>.currentOrNull: T?
    get() {
        val ref = compositionReference()
        val ambients = ref.getAmbientScope()
        return ambients[this as Ambient<Any?>]?.value as? T
            ?: AmbientAccessor.getDefaultValue(this) as? T
    }

val <T : Any> Ambient<T>.currentOrThrow: T
    get() =
        currentOrNull ?: error("No value provided for $this")

inline fun <T : Any> Ambient<T>.currentOrElse(defaultValue: () -> T): T =
    currentOrNull ?: defaultValue()