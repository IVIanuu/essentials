package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.animation.*

fun OpenCloseStackTransition(): StackTransition = {
    attachTo()
    val speed = 1f

    suspend fun animate(
        child: AnimatedStackChild<*>?,
        alphaDelay: Int,
        startScale: Float,
        endScale: Float,
        startAlpha: Float,
        endAlpha: Float
    ) {
        if (child == null) return
        par(
            {
                val alphaModifier = elementModifier(child, ContentAnimationElementKey)!!
                animate(tween(
                    durationMillis = (alphaDelay * speed).toInt(),
                    delayMillis = (alphaDelay * speed).toInt(),
                    easing = LinearEasing
                )) {
                    alphaModifier.value = Modifier.alpha(lerp(startAlpha, endAlpha, value))
                }
            },
            {
                val toScaleModifier = elementModifier(child, ContentAnimationElementKey)!!
                animate(
                    startScale,
                    endScale,
                    animationSpec = tween(durationMillis = (300 * speed).toInt())
                ) { value, _ -> toScaleModifier.value = Modifier.scale(value) }
            }
        )
    }

    if (isPush) {
        par(
            {
                animate(
                    child = to,
                    alphaDelay = 50,
                    startAlpha = 0f,
                    endAlpha = 1f,
                    startScale = 0.85f,
                    endScale = 1f
                )
            },
            {
                animate(
                    child = from,
                    alphaDelay = 50,
                    startAlpha = 1f,
                    endAlpha = 0f,
                    startScale = 1f,
                    endScale = 1.15f
                )
            }
        )
    } else {
        par(
            {
                animate(
                    child = to,
                    alphaDelay = 66,
                    startAlpha = 0f,
                    endAlpha = 1f,
                    startScale = 1.1f,
                    endScale = 1f
                )
            },
            {
                animate(
                    child = from,
                    alphaDelay = 66,
                    startAlpha = 1f,
                    endAlpha = 0f,
                    startScale = 1f,
                    endScale = 0.9f
                )
            }
        )
    }
    detachFrom()
}
