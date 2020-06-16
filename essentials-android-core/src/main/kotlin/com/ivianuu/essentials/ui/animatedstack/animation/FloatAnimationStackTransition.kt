package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import androidx.animation.Easing
import androidx.animation.FastOutSlowInEasing
import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.animation.animatedFloat
import com.ivianuu.essentials.ui.animatable.Animatable
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun defaultAnimationBuilder(
    duration: Duration = 300.milliseconds,
    easing: Easing = FastOutSlowInEasing,
    delay: Duration = 0.milliseconds
): AnimationBuilder<Float> = TweenBuilder<Float>().apply {
    this.duration = duration.toLongMilliseconds().toInt()
    this.easing = easing
    this.delay = delay.toLongMilliseconds().toInt()
}

fun FloatAnimationStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder(),
    apply: @Composable (
        fromAnimatable: Animatable?,
        toAnimatable: Animatable?,
        isPush: Boolean,
        progress: Float
    ) -> Unit
): StackTransition = { context ->
    val animation = animatedFloat(0f)
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
