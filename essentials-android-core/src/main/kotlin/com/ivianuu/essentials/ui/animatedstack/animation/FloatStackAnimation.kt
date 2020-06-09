package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.ui.animation.animatedFloat
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.animatedstack.StackAnimationModifiers
import kotlin.time.Duration

fun FloatStackAnimation(
    duration: Duration,
    createModifiers: @Composable (
        hasFrom: Boolean,
        hasTo: Boolean,
        isPush: Boolean,
        progress: Float
    ) -> StackAnimationModifiers
): StackAnimation = { hasFrom, hasTo, isPush, onComplete ->
    val animation = animatedFloat(0f)
    onActive {
        animation.animateTo(
            targetValue = 1f,
            anim = TweenBuilder<Float>().apply {
                this.duration = duration.toLongMilliseconds().toInt()
            },
            onEnd = { _, _ -> onComplete() }
        )
    }

    createModifiers(
        hasFrom,
        hasTo,
        isPush,
        animation.value
    )
}
