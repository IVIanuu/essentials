package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.layout.offsetFraction
import kotlin.time.Duration
import kotlin.time.milliseconds

fun VerticalStackTransition(duration: Duration = 300.milliseconds): StackTransition {
    return FloatAnimationStackTransition(duration = duration) { fromElement, toElement, isPush, progress ->
        toElement?.animationModifier = if (isPush) Modifier.offsetFraction(y = 1f - progress)
        else Modifier
        fromElement?.animationModifier = if (!isPush) Modifier.offsetFraction(y = progress)
        else Modifier
    }
}
