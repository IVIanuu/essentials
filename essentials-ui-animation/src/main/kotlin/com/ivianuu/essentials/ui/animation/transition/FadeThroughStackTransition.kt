package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.util.*

fun FadeThroughStackTransition(
    spec: AnimationSpec<Float> = defaultAnimationSpec()
): StackTransition = {
    attachTo()
    val from = fromElementModifier(ContentAnimationElementKey)
    val to = toElementModifier(ContentAnimationElementKey)
    animate(spec) { value ->
        if (isPush) {
            to?.value = Modifier
                .alpha(LinearOutSlowInEasing.transform(interval(0.3f, 1f, value)))
                .scale(
                    lerp(
                        0.92f,
                        1f,
                        LinearOutSlowInEasing.transform(interval(0.3f, 1f, value))
                    )
                )
        }
        from?.value = Modifier.alpha(lerp(1f, 0f, FastOutLinearInEasing.transform(value)))
    }
}
