package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import com.ivianuu.essentials.ui.animatable.Alpha
import com.ivianuu.essentials.ui.animatable.FractionOffsetY
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun VerticalFadeStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder()
): StackTransition {
    return FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
        if (to != null && isPush) {
            to
                .set(Alpha, progress)
                .set(FractionOffsetY, 0.3f * (1f - progress))
        }
        if (from != null && !isPush) {
            from
                .set(Alpha, 1f - progress)
                .set(FractionOffsetY, 0.3f * progress)
        }
    }
}
