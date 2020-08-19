package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.animation.core.AnimationSpec
import com.ivianuu.essentials.ui.animatable.ScaleX
import com.ivianuu.essentials.ui.animatable.ScaleY
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun ScaleStackTransition(
    anim: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition {
    return FloatAnimationStackTransition(anim = anim) { from, to, isPush, progress ->
        if (to != null && isPush) {
            to
                .set(ScaleX, progress)
                .set(ScaleY, progress)
        }

        if (from != null && !isPush) {
            from
                .set(ScaleX, 1f - progress)
                .set(ScaleY, 1f - progress)
        }
    }
}
