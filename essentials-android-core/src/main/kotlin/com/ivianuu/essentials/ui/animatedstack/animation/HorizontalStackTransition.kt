package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import com.ivianuu.essentials.ui.animatable.setFractionTranslationX
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun HorizontalStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder()
): StackTransition = FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
    to?.setFractionTranslationX(if (isPush) (1f - progress) else -1f + progress)
    from?.setFractionTranslationX(if (isPush) -progress else progress)
}
