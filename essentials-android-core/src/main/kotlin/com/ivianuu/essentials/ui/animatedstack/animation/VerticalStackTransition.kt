package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import com.ivianuu.essentials.ui.animatable.FractionOffsetY
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun VerticalStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder()
): StackTransition {
    return FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
        if (isPush) to?.set(FractionOffsetY, 1f - progress)
        else from?.set(FractionOffsetY, progress)
    }
}
