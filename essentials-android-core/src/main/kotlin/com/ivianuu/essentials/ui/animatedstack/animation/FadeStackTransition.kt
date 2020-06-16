package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationBuilder
import com.ivianuu.essentials.ui.animatable.Alpha
import kotlin.time.milliseconds

fun FadeStackTransition(
    anim: AnimationBuilder<Float> = defaultAnimationBuilder(duration = 180.milliseconds)
) = FloatAnimationStackTransition(anim = anim) { from, to, _, progress ->
    to?.set(Alpha, progress)
    from?.set(Alpha, 1f - progress)
}
