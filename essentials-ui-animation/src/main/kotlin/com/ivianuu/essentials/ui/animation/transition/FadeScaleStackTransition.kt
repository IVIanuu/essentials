package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.util.*
import kotlin.time.*

object ScrimAnimationElementKey

object PopupAnimationElementKey

fun FadeScaleStackTransition(
    origin: TransformOrigin = TransformOrigin.Center,
    enterSpec: AnimationSpec<Float> = defaultAnimationSpec(150.milliseconds),
    exitSpec: AnimationSpec<Float> = defaultAnimationSpec(75.milliseconds)
): StackTransition = {
    attachTo()
    if (isPush) {
        val scrim = toElementModifier(ScrimAnimationElementKey)
        val popup = toElementModifier(PopupAnimationElementKey)
        animate(enterSpec) {
            scrim?.value = Modifier.alpha(value)
            popup?.value = Modifier
                .graphicsLayer {
                    transformOrigin = origin
                    alpha = interval(0f, 0.3f, value)
                    scaleX = lerp(0.8f, 1f, AccelerateEasing.transform(value))
                    scaleY = lerp(0.8f, 1f, AccelerateEasing.transform(value))
                }
        }
    } else {
        val scrim = fromElementModifier(ScrimAnimationElementKey)
        val popup = fromElementModifier(PopupAnimationElementKey)
        animate(exitSpec) {
            scrim?.value = Modifier.alpha(1f - value)
            popup?.value = Modifier.alpha(1f - value)
        }
    }
    detachFrom()
}
