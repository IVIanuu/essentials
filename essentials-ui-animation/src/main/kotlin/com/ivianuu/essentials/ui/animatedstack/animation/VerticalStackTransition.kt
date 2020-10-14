package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.animation.core.AnimationSpec
import com.ivianuu.essentials.ui.animatable.setFractionTranslationY
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun VerticalStackTransition(
    anim: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition {
    return FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
        if (isPush) to
            ?.setFractionTranslationY(1f - progress)
        else from
            ?.setFractionTranslationY(progress)
    }
}
