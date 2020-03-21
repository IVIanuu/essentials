package com.ivianuu.essentials.ui.material

import androidx.animation.AnimationClockObservable
import androidx.compose.Composable
import androidx.compose.CompositionLifecycleObserver
import androidx.compose.Immutable
import androidx.compose.StructurallyEqual
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import androidx.ui.animation.asDisposableClock
import androidx.ui.animation.transitionsEnabled
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.Constraints
import androidx.ui.core.DensityAmbient
import androidx.ui.core.DrawModifier
import androidx.ui.core.LayoutDirection
import androidx.ui.core.LayoutModifier
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.PressIndicatorGestureDetector
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.RippleEffect
import androidx.ui.material.ripple.RippleEffectFactory
import androidx.ui.material.ripple.RippleThemeAmbient
import androidx.ui.unit.Density
import androidx.ui.unit.Dp
import androidx.ui.unit.IntPxSize
import androidx.ui.unit.PxPosition
import androidx.ui.unit.PxSize
import androidx.ui.unit.center
import androidx.ui.unit.ipx
import androidx.ui.unit.toPxSize
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class RippleStyle(val color: Color)

@Composable
fun DefaultRippleStyle(
    color: Color = defaultRippleColor()
) = RippleStyle(color = color)

val RippleStyleAmbient = staticAmbientOf<RippleStyle>()

@Composable
fun defaultRippleColor() = with(MaterialTheme.colors()) {
    onSurface.copy(alpha = if (isLight) 0.12f else 0.24f)
}

@Composable
fun ripple(
    style: RippleStyle = RippleStyleAmbient.currentOrElse { DefaultRippleStyle() },
    bounded: Boolean = true,
    radius: Dp? = null,
    enabled: Boolean = true,
    clock: AnimationClockObservable = AnimationClockAmbient.current
): Modifier {
    @Suppress("NAME_SHADOWING") // don't allow usage of the parameter clock, only the disposable
    val clock = clock.asDisposableClock()
    val density = DensityAmbient.current
    val rippleModifier = remember { RippleModifier() }
    val theme = RippleThemeAmbient.current
    rippleModifier.color = style.color

    val pressIndicator = PressIndicatorGestureDetector(
        onStart = { position ->
            if (enabled && transitionsEnabled) {
                rippleModifier.handleStart(position, theme.factory, density, bounded, radius, clock)
            }
        },
        onStop = { rippleModifier.handleFinish(false) },
        onCancel = { rippleModifier.handleFinish(true) }
    )
    return pressIndicator + rippleModifier
}

private class RippleModifier : DrawModifier, LayoutModifier, CompositionLifecycleObserver {

    var color: Color by mutableStateOf(Color.Transparent, StructurallyEqual)

    private var size: IntPxSize = IntPxSize(0.ipx, 0.ipx)
    private var effects = mutableListOf<RippleEffect>()
    private var currentEffect: RippleEffect? = null

    private var animationPulse by mutableStateOf(0L)
    private val redraw: () -> Unit = { animationPulse++ }

    override fun Density.modifySize(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
        childSize: IntPxSize
    ): IntPxSize {
        size = childSize
        return childSize
    }

    fun handleStart(
        touchPosition: PxPosition,
        factory: RippleEffectFactory,
        density: Density,
        bounded: Boolean,
        radius: Dp?,
        clock: AnimationClockObservable
    ) {
        val position = if (bounded) touchPosition else size.toPxSize().center()
        val onAnimationFinished = { effect: RippleEffect ->
            effects.remove(effect)
            if (currentEffect == effect) {
                currentEffect = null
            }
        }
        val effect = factory.create(
            size,
            position,
            density,
            radius,
            bounded,
            clock,
            redraw,
            onAnimationFinished
        )

        effects.add(effect)
        currentEffect = effect
        redraw()
    }

    fun handleFinish(canceled: Boolean) {
        currentEffect?.finish(canceled)
        currentEffect = null
    }

    override fun draw(density: Density, drawContent: () -> Unit, canvas: Canvas, size: PxSize) {
        drawContent()
        animationPulse // model read
        effects.forEach { it.draw(canvas, this.size, color) }
    }

    override fun onEnter() {
        // do nothing
    }

    override fun onLeave() {
        effects.forEach { it.dispose() }
        effects.clear()
        currentEffect = null
    }
}
