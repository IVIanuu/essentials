package com.ivianuu.essentials.ui.animatable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@Stable
interface ModifierProp<T> : Prop<T> {
    fun asModifier(value: T): Modifier
}

inline fun <T> modifierProp(crossinline apply: (T) -> Modifier): ModifierProp<T> {
    return object : ModifierProp<T> {
        override fun asModifier(value: T): Modifier = apply.invoke(value)
    }
}

inline fun <T> composedModifierProp(crossinline apply: @Composable (T) -> Modifier): ModifierProp<T> =
    modifierProp { value -> Modifier.composed { apply(value) } }
