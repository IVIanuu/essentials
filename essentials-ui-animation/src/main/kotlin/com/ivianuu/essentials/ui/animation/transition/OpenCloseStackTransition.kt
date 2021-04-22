package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.animation.*

fun OpenCloseStackTransition(): StackTransition = {
    attachTo()
    val speed = 1f
    val fromModifier = fromElementModifier(ContentAnimationElementKey)
    val toModifier = toElementModifier(ContentAnimationElementKey)
    if (isPush) {
        par(
            {
                if (toModifier != null) {
                    par(
                        {
                            val toAlphaModifier = toElementModifier(ContentAnimationElementKey)!!
                            animate(tween(
                                durationMillis = (50 * speed).toInt(),
                                delayMillis = (50 * speed).toInt(),
                                easing = LinearEasing
                            )) {
                                toAlphaModifier.value = Modifier.alpha(value)
                            }
                        },
                        {
                            val toScaleModifier = toElementModifier(ContentAnimationElementKey)!!
                            animate(
                                0.85f,
                                1f,
                                animationSpec = tween(durationMillis = (300 * speed).toInt())
                            ) { value, _ -> toScaleModifier.value = Modifier.scale(value) }
                        }
                    )
                }
            },
            {
                if (fromModifier != null) {
                    par(
                        {
                            val fromAlphaModifier = fromElementModifier(ContentAnimationElementKey)!!
                            animate(tween(
                                durationMillis = (50 * speed).toInt(),
                                delayMillis = (50 * speed).toInt(),
                                easing = LinearEasing
                            )) {
                                fromAlphaModifier.value = Modifier.alpha(1f - value)
                            }
                        },
                        {
                            val fromScaleModifier = fromElementModifier(ContentAnimationElementKey)!!
                            animate(
                                1f,
                                1.15f,
                                animationSpec = tween(durationMillis = (300 * speed).toInt())
                            ) { value, _ -> fromScaleModifier.value = Modifier.scale(value) }
                        }
                    )
                }
            }
        )
    } else {
        par(
            {
                if (toModifier != null) {
                    par(
                        {
                            val toAlphaModifier = toElementModifier(ContentAnimationElementKey)!!
                            animate(tween(
                                durationMillis = (50 * speed).toInt(),
                                delayMillis = (66 * speed).toInt(),
                                easing = LinearEasing
                            )) {
                                toAlphaModifier.value = Modifier.alpha(value)
                            }
                        },
                        {
                            val toScaleModifier = toElementModifier(ContentAnimationElementKey)!!
                            animate(
                                1.1f,
                                1f,
                                animationSpec = tween(durationMillis = (300 * speed).toInt())
                            ) { value, _ -> toScaleModifier.value = Modifier.scale(value) }
                        }
                    )
                }
            },
            {
                if (fromModifier != null) {
                    par(
                        {
                            val fromAlphaModifier = fromElementModifier(ContentAnimationElementKey)!!
                            animate(tween(
                                durationMillis = (50 * speed).toInt(),
                                delayMillis = (66 * speed).toInt(),
                                easing = LinearEasing
                            )) {
                                fromAlphaModifier.value = Modifier.alpha(1f - value)
                            }
                        },
                        {
                            val fromScaleModifier = fromElementModifier(ContentAnimationElementKey)!!
                            animate(
                                1f,
                                0.9f,
                                animationSpec = tween(durationMillis = (300 * speed).toInt())
                            ) { value, _ -> fromScaleModifier.value = Modifier.scale(value) }
                        }
                    )
                }
            }
        )
    }
    detachFrom()
}
