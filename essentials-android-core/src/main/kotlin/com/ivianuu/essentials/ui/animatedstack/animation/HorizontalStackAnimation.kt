package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.animatedstack.StackAnimationModifiers
import com.ivianuu.essentials.ui.layout.offsetFraction
import kotlin.time.Duration
import kotlin.time.milliseconds

fun HorizontalStackAnimation(duration: Duration = 300.milliseconds): StackAnimation {
    return FloatStackAnimation(duration = duration) { _, _, isPush, progress ->
        StackAnimationModifiers(
            to = {
                Modifier.offsetFraction(
                    x = if (isPush) 1f - progress else -1f + progress
                )
            },
            from = {
                Modifier.offsetFraction(
                    x = if (isPush) -progress else progress
                )
            }
        )
    }
}
