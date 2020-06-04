package com.ivianuu.essentials.ui.common

import androidx.animation.AnimationBuilder
import androidx.animation.AnimationClockObservable
import androidx.animation.AnimationClockObserver
import androidx.animation.AnimationEndReason
import androidx.compose.Composable
import androidx.compose.StructurallyEqual
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.ui.animation.AnimatedFloatModel
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.Direction
import androidx.ui.core.Modifier
import androidx.ui.core.PassThroughLayout
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.dragGestureFilter
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.unit.PxPosition
import com.ivianuu.essentials.ui.core.Axis
import com.ivianuu.essentials.ui.core.retain

// todo remove

@Composable
fun RetainedScrollableState(
    initial: Float = 0f,
    minValue: Float = 0f,
    maxValue: Float = Float.MAX_VALUE,
    animationClock: AnimationClockObservable = AnimationClockAmbient.current
): ScrollableState {
    val flingConfig = FlingConfig()
    return retain {
        ScrollableState(
            animationClock,
            initial, minValue, maxValue,
            flingConfig
        )
    }
}

class ScrollableState(
    animationClock: AnimationClockObservable,
    initial: Float = 0f,
    minValue: Float = 0f,
    maxValue: Float = Float.MAX_VALUE,
    val flingConfig: FlingConfig
) {

    val value: Float
        get() = animatedFloat.value
            .coerceIn(_minValue, _maxValue) // todo remove once fixed

    private var _minValue by mutableStateOf(minValue)
    val minValue: Float get() = _minValue

    private var _maxValue by mutableStateOf(maxValue)
    val maxValue: Float get() = _maxValue

    var isAnimating: Boolean by mutableStateOf(false, StructurallyEqual)
        private set

    private val clocksProxy = object : AnimationClockObservable {
        override fun subscribe(observer: AnimationClockObserver) {
            isAnimating = true
            animationClock.subscribe(observer)
        }

        override fun unsubscribe(observer: AnimationClockObserver) {
            isAnimating = false
            animationClock.unsubscribe(observer)
        }
    }

    private val animatedFloat = AnimatedFloatModel(initial, clocksProxy).apply {
        setBounds(minValue, maxValue)
    }

    fun smoothScrollTo(
        value: Float,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        animatedFloat.animateTo(value, anim, onEnd)
    }

    fun smoothScrollBy(
        value: Float,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(this.value + value, anim, onEnd)
    }

    fun scrollTo(value: Float) {
        animatedFloat.snapTo(value.coerceIn(_minValue, _maxValue))
    }

    fun scrollBy(value: Float) {
        scrollTo(this.value + value)
    }

    fun correctBy(value: Float) {
        scrollBy(value)
    }

    fun flingBy(velocity: Float) {
        animatedFloat.fling(
            flingConfig,
            velocity
        )
    }

    fun updateBounds(minValue: Float = _minValue, maxValue: Float = _maxValue) {
        check(minValue <= maxValue) {
            "Min value $minValue cannot be greater than max value $maxValue"
        }
        _minValue = minValue
        _maxValue = maxValue
        animatedFloat.setBounds(minValue, maxValue)
        scrollTo(value.coerceIn(_minValue, _maxValue))
    }
}

@Composable
fun Scrollable(
    state: ScrollableState,
    direction: Axis = Axis.Vertical,
    enabled: Boolean = true,
    children: @Composable () -> Unit
) {
    val drag = Modifier.dragGestureFilter(
        dragObserver = object : DragObserver {
            override fun onDrag(dragDistance: PxPosition): PxPosition {
                if (!enabled) return PxPosition.Origin
                val oldValue = state.value
                val distance = -when (direction) {
                    Axis.Horizontal -> dragDistance.x
                    Axis.Vertical -> dragDistance.y
                }
                val newValue =
                    (oldValue + distance).coerceIn(state.minValue, state.maxValue)

                val consumed = -(newValue - oldValue)
                state.scrollTo(newValue)

                return when (direction) {
                    Axis.Horizontal -> PxPosition(
                        x = consumed,
                        y = 0f
                    )
                    Axis.Vertical -> PxPosition(
                        x = 0f,
                        y = consumed
                    )
                }
            }

            override fun onStop(velocity: PxPosition) {
                if (!enabled) return
                val mainAxisVelocity = -when (direction) {
                    Axis.Horizontal -> velocity.x
                    Axis.Vertical -> velocity.y
                }
                state.flingBy(mainAxisVelocity)
            }
        },
        canDrag = { dragDirection ->
            if (!enabled) return@dragGestureFilter false
            return@dragGestureFilter when (direction) {
                Axis.Horizontal -> dragDirection == Direction.LEFT || dragDirection == Direction.RIGHT
                Axis.Vertical -> dragDirection == Direction.UP || dragDirection == Direction.DOWN
            }
        },
        startDragImmediately = state.isAnimating
    )

    // TODO(b/150706555): This layout is temporary and should be removed once Semantics
    //  is implemented with modifiers.
    @Suppress("DEPRECATION")
    PassThroughLayout(drag, children)
}
