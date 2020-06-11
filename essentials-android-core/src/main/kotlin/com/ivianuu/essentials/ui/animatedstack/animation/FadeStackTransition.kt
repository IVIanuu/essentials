package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import com.ivianuu.essentials.ui.animatable.Alpha

fun FadeStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder()
) = FloatAnimationStackTransition(anim = anim) { from, to, _, progress ->
    to?.set(Alpha, progress)
    from?.set(Alpha, 1f - progress)
}
