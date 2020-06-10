package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.Easing
import androidx.animation.FastOutSlowInEasing
import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.ui.animation.animatedFloat
import com.ivianuu.essentials.ui.animatable.AnimatableElement
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun FloatAnimationStackTransition(
    duration: Duration = 300.milliseconds,
    delay: Duration = 0.milliseconds,
    easing: Easing = FastOutSlowInEasing,
    apply: @Composable (
        fromElement: AnimatableElement?,
        toElement: AnimatableElement?,
        isPush: Boolean,
        progress: Float
    ) -> Unit
): StackTransition = { context ->
    val animation = animatedFloat(0f)

    onActive {
        if (context.toElement != null) context.addTo()
        animation.animateTo(
            targetValue = 1f,
            anim = TweenBuilder<Float>().apply {
                this.duration = duration.toLongMilliseconds().toInt()
                this.easing = easing
                this.delay = delay.toLongMilliseconds().toInt()
            },
            onEnd = { _, _ ->
                if (context.fromElement != null) context.removeFrom()
                context.onComplete()
            }
        )
    }

    apply(context.fromElement, context.toElement, context.isPush, animation.value)
}
