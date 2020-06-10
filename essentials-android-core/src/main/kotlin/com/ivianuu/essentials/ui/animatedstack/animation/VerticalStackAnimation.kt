package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.layout.offsetFraction
import kotlin.time.Duration
import kotlin.time.milliseconds

fun VerticalStackAnimation(duration: Duration = 300.milliseconds): StackAnimation {
    return FloatStackAnimation(duration = duration) { fromElement, toElement, isPush, progress ->
        toElement?.animationModifier = if (isPush) Modifier.offsetFraction(y = 1f - progress)
        else Modifier
        fromElement?.animationModifier = if (!isPush) Modifier.offsetFraction(y = progress)
        else Modifier
    }
}
