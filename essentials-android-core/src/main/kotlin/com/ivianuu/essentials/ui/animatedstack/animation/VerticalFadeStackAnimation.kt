package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.animatedstack.StackAnimationModifiers
import com.ivianuu.essentials.ui.layout.offsetFraction
import kotlin.time.Duration
import kotlin.time.milliseconds

fun VerticalFadeStackAnimation(duration: Duration = 300.milliseconds): StackAnimation {
    return FloatStackAnimation(duration = duration) { progress ->
        StackAnimationModifiers(
            to = {
                if (isPush) Modifier
                    .offsetFraction(y = 0.3f * (1f - progress))
                    .drawOpacity(opacity = progress) else Modifier
            },
            from = {
                if (!isPush) Modifier
                    .offsetFraction(y = 0.3f * progress)
                    .drawOpacity(opacity = 1f - progress) else Modifier
            }
        )
    }
}
