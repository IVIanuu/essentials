package com.ivianuu.essentials.ui.animatable

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.ui.core.Modifier
import androidx.ui.core.composed
import com.ivianuu.essentials.ui.layout.offsetFraction

@Stable
interface ModifierProp<T> : Prop<T> {
    fun asModifier(value: T): Modifier
}

val FractionOffsetX = modifierProp<Float> { Modifier.offsetFraction(x = it) }
val FractionOffsetY = modifierProp<Float> { Modifier.offsetFraction(y = it) }

inline fun <T> modifierProp(crossinline apply: (T) -> Modifier): ModifierProp<T> {
    return object : ModifierProp<T> {
        override fun asModifier(value: T): Modifier = apply.invoke(value)
    }
}

inline fun <T> composedModifierProp(crossinline apply: @Composable (T) -> Modifier): ModifierProp<T> =
    modifierProp { value -> Modifier.composed { apply(value) } }
