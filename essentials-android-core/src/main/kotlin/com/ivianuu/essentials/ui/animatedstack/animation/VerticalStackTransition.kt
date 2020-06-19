package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import com.ivianuu.essentials.ui.animatable.setFractionTranslationY
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun VerticalStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder()
): StackTransition {
    return FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
        if (isPush) to
            ?.setFractionTranslationY(1f - progress)
        else from
            ?.setFractionTranslationY(progress)
    }
}
