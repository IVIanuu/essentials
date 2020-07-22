package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationSpec
import com.ivianuu.essentials.ui.animatable.setFractionTranslationX
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun HorizontalStackTransition(
    anim: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition = FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
    to?.setFractionTranslationX(if (isPush) (1f - progress) else -1f + progress)
    from?.setFractionTranslationX(if (isPush) -progress else progress)
}
