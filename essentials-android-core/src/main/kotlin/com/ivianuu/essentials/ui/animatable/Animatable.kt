package com.ivianuu.essentials.ui.animatable

import androidx.compose.frames.modelMapOf
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.Modifier
import androidx.ui.core.composed
import androidx.ui.core.globalBounds
import androidx.ui.core.onPositioned
import androidx.ui.unit.PxBounds

class Animatable(val tag: Any) {

    private val _props = modelMapOf<Prop<*>, Any?>()
    val props: Map<Prop<*>, Any?> get() = _props

    private val modifiersByProps = mutableMapOf<Prop<*>, Modifier>()

    private val onPositioned = Modifier.onPositioned {
        layoutCoordinates = it
    }
    private val drawLayerModifier = MutableDrawLayerModifier()
    private val baseModifier = onPositioned + drawLayerModifier

    internal var modifier by mutableStateOf(baseModifier)

    operator fun <T> get(prop: Prop<T>): T = props.getValue(prop) as T

    operator fun <T> set(prop: Prop<T>, value: T): Animatable {
        _props[prop] = value
        if (prop is DrawLayerProp) {
            prop.apply(drawLayerModifier, value)
        }
        if (prop is ModifierProp) {
            modifiersByProps[prop] = prop.asModifier(value)
            modifier = baseModifier + (modifiersByProps.values
                .reduceOrNull { acc, modifier -> acc + modifier } ?: Modifier)
        }
        return this
    }

    fun <T> getOrNull(prop: Prop<T>): T? = _props[prop] as? T

    inline fun <T> getOrElse(prop: Prop<T>, defaultValue: () -> T): T =
        if (prop in props) get(prop) else defaultValue()

    var layoutCoordinates: LayoutCoordinates? by mutableStateOf(null)
        internal set

    val bounds: PxBounds?
        get() = layoutCoordinates
            ?.takeIf { it.isAttached }
            ?.globalBounds

}

fun Modifier.animatable(
    tag: Any,
    vararg props: PropWithValue<*>
): Modifier = composed {
    val animatable = animatableFor(tag)
    remember(*props) {
        props.forEach { animatable[it.prop as Prop<Any?>] = it.value }
    }
    animatable.modifier
}
