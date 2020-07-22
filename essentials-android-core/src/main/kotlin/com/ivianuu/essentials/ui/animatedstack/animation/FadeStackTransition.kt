package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.AnimationSpec
import com.ivianuu.essentials.ui.animatable.Alpha
import kotlin.time.milliseconds

fun FadeStackTransition(
    anim: AnimationSpec<Float> = defaultAnimationSpec(duration = 180.milliseconds)
) = FloatAnimationStackTransition(anim = anim) { from, to, _, progress ->
    to?.set(Alpha, progress)
    from?.set(Alpha, 1f - progress)
}
