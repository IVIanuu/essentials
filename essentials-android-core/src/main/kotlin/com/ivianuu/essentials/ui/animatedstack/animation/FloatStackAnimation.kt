package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.Easing
import androidx.animation.FastOutSlowInEasing
import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.ui.animation.animatedFloat
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.animatedstack.StackAnimationContext
import com.ivianuu.essentials.ui.animatedstack.StackAnimationModifiers
import kotlin.time.Duration
import kotlin.time.milliseconds

fun FloatStackAnimation(
    duration: Duration = 300.milliseconds,
    delay: Duration = 0.milliseconds,
    easing: Easing = FastOutSlowInEasing,
    createModifiers: @Composable StackAnimationContext.(progress: Float) -> StackAnimationModifiers
): StackAnimation = {
    val animation = animatedFloat(0f)
    onActive {
        animation.animateTo(
            targetValue = 1f,
            anim = TweenBuilder<Float>().apply {
                this.duration = duration.toLongMilliseconds().toInt()
                this.easing = easing
                this.delay = delay.toLongMilliseconds().toInt()
            },
            onEnd = { _, _ -> onComplete() }
        )
    }

    createModifiers(this, animation.value)
}
