package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationSpec
import androidx.animation.Easing
import androidx.animation.FastOutSlowInEasing
import androidx.animation.TweenSpec
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.animation.animatedFloat
import com.ivianuu.essentials.ui.animatable.Alpha
import com.ivianuu.essentials.ui.animatable.Animatable
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun defaultAnimationSpec(
    duration: Duration = 300.milliseconds,
    delay: Duration = 0.milliseconds,
    easing: Easing = FastOutSlowInEasing
): AnimationSpec<Float> = TweenSpec<Float>(
    durationMillis = duration.toLongMilliseconds().toInt(),
    delay = delay.toLongMilliseconds().toInt(),
    easing = easing
)

fun FloatAnimationStackTransition(
    anim: AnimationSpec<Float> = defaultAnimationSpec(),
    apply: @Composable (
        fromAnimatable: Animatable?,
        toAnimatable: Animatable?,
        isPush: Boolean,
        progress: Float
    ) -> Unit
): StackTransition = { context ->
    val animation = animatedFloat(0f)
    context.toAnimatable?.set(Alpha, if (animation.value == 0f) 0f else 1f)
    remember {
        if (context.toAnimatable != null) context.addTo()
        animation.animateTo(
            targetValue = 1f,
            anim = anim,
            onEnd = { _, _ ->
                if (context.fromAnimatable != null) context.removeFrom()
                context.onComplete()
            }
        )
    }
    apply(
        context.fromAnimatable,
        context.toAnimatable,
        context.isPush,
        animation.value
    )
}
