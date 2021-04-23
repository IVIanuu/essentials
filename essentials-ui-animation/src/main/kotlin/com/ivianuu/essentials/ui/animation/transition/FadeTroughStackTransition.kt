package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.util.*

fun FadeTroughStackTransition(
    spec: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition = {
    attachTo()
    if (isPush) {
        val content = toElementModifier(ContentAnimationElementKey)
        animate(spec) {
            content?.value = Modifier
                .alpha(
                    interval(0f, 0.33f, AccelerateEasing.transform(value))
                )
                .scale(
                    interval(
                        0.33f,
                        1f,
                        lerp(0.92f, 1f, AccelerateEasing.transform(value))
                    )
                )
        }
    } else {
        val content = fromElementModifier(ContentAnimationElementKey)
        animate(spec) {
            content?.value = Modifier.alpha(FastOutLinearInEasing.transform(
                1f - value
            ))
        }
    }
    detachFrom()
}
