package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.layout.offsetFraction
import kotlin.time.Duration
import kotlin.time.milliseconds

fun HorizontalStackTransition(duration: Duration = 300.milliseconds): StackTransition {
    return FloatAnimationStackTransition(duration = duration) { fromElement, toElement, isPush, progress ->
        toElement?.animationModifier =
            Modifier.offsetFraction(x = if (isPush) 1f - progress else -1f + progress)
        fromElement?.animationModifier =
            Modifier.offsetFraction(x = if (isPush) -progress else progress)
    }
}
