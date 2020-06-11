package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import com.ivianuu.essentials.ui.animatable.FractionOffsetX
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun HorizontalStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder()
): StackTransition =
    FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
        to?.set(FractionOffsetX, if (isPush) 1f - progress else -1f + progress)
        from?.set(FractionOffsetX, if (isPush) -progress else progress)
    }
