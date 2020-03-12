package com.ivianuu.essentials.android.ui.common

import androidx.animation.AnimationBuilder
import androidx.animation.AnimationClockObservable
import androidx.animation.AnimationClockObserver
import androidx.animation.AnimationEndReason
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.mutableStateOf
import androidx.ui.animation.AnimatedFloatModel
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.Direction
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.TouchSlopDragGestureDetector
import androidx.ui.foundation.animation.FlingConfig
import androidx.ui.foundation.animation.fling
import androidx.ui.unit.Px
import androidx.ui.unit.PxPosition
import androidx.ui.unit.coerceIn
import androidx.ui.unit.px
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.android.ui.core.Axis
import com.ivianuu.essentials.android.ui.core.retain

// todo remove

@Composable
fun RetainedScrollableState(
    initial: Px = 0.px,
    minValue: Px = 0.px,
    maxValue: Px = Px.Infinity,
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

@Model
class ScrollableState(
    animationClock: AnimationClockObservable,
    initial: Px = 0.px,
    minValue: Px = 0.px,
    maxValue: Px = Px.Infinity,
    val flingConfig: FlingConfig
) {

    val value: Px
        get() = animatedFloat.value.px
            .coerceIn(_minValue, _maxValue) // todo remove once fixed

    private var _minValue = minValue
    val minValue: Px get() = _minValue

    private var _maxValue = maxValue
    val maxValue: Px get() = _maxValue

    var isAnimating: Boolean by mutableStateOf(false)
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

    private val animatedFloat = AnimatedFloatModel(initial.value, clocksProxy).apply {
        setBounds(minValue.value, maxValue.value)
    }

    fun smoothScrollTo(
        value: Px,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        animatedFloat.animateTo(value.value, anim, onEnd)
    }

    fun smoothScrollBy(
        value: Px,
        anim: AnimationBuilder<Float>,
        onEnd: (endReason: AnimationEndReason, finishValue: Float) -> Unit = { _, _ -> }
    ) {
        smoothScrollTo(this.value + value, anim, onEnd)
    }

    fun scrollTo(value: Px) {
        if (this.value != value) d { "scroll to $value" }
        animatedFloat.snapTo(value.value.coerceIn(_minValue.value, _maxValue.value))
    }

    fun scrollBy(value: Px) {
        scrollTo(this.value + value)
    }

    fun correctBy(value: Px) {
        scrollBy(value)
    }

    fun flingBy(velocity: Px) {
        d { "fling by $velocity" }
        animatedFloat.fling(
            flingConfig,
            velocity.value
        )
    }

    fun updateBounds(minValue: Px = _minValue, maxValue: Px = _maxValue) {
        check(minValue <= maxValue) {
            "Min value $minValue cannot be greater than max value $maxValue"
        }
        _minValue = minValue
        _maxValue = maxValue
        animatedFloat.setBounds(minValue.value, maxValue.value)
        scrollTo(value.coerceIn(_minValue, _maxValue))

        d { "update bounds $minValue $maxValue" }
    }
}

@Composable
fun Scrollable(
    state: ScrollableState,
    direction: Axis = Axis.Vertical,
    enabled: Boolean = true,
    children: @Composable () -> Unit
) {
    TouchSlopDragGestureDetector(
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
                        y = 0.px
                    )
                    Axis.Vertical -> PxPosition(
                        x = 0.px,
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
            if (!enabled) return@TouchSlopDragGestureDetector false
            return@TouchSlopDragGestureDetector when (direction) {
                Axis.Horizontal -> dragDirection == Direction.LEFT || dragDirection == Direction.RIGHT
                Axis.Vertical -> dragDirection == Direction.UP || dragDirection == Direction.DOWN
            }
        },
        startDragImmediately = state.isAnimating,
        children = children
    )
}
