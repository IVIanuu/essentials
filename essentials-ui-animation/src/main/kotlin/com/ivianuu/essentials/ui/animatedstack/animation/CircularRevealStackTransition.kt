/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun CircularRevealStackTransition(
    origin: Any,
    color: Color = Color.Unspecified,
    duration: Duration = 300.milliseconds,
): StackTransition = { context ->
    /*if (context.toAnimatable != null) onActive { context.addTo() }

    val originElement = AnimatableRootAmbient.current.getAnimatable(origin)
    val capturedOriginBounds = originElement.capturedBounds()

    val centerPosition = capturedOriginBounds?.center()
    val startRadius = if (context.isPush) 0f else centerPosition?.let { hypot(it.x, it.y) } ?: 0f
    val endRadius = if (context.isPush) centerPosition?.let { hypot(it.x, it.y) } ?: 0f else 0f

    val animation = animatedFloat(0f)

    context.toAnimatable?.drawLayerModifier?.alpha = animation.value
    context.fromAnimatable?.drawLayerModifier?.alpha = 1f - animation.value

    if (capturedOriginBounds != null) {
        onActive {
            animation.animateTo(
                targetValue = 1f,
                anim = TweenBuilder<Float>().apply {
                    this.duration = duration.toLongMilliseconds().toInt()
                },
                onEnd = { _, _ ->
                    if (context.fromAnimatable != null) context.removeFrom()
                    context.fromAnimatable?.animationModifier = Modifier
                    context.toAnimatable?.animationModifier = Modifier
                    context.onComplete()
                }
            )
        }
    }

    val finalColor = color.useOrElse { MaterialTheme.colors.surface }

    val drawModifier = Modifier
        .drawWithContent {
            drawContent()
            drawCircle(
                color = finalColor,
                radius = lerp(
                    startRadius,
                    endRadius,
                    animation.value
                ),
                center = centerPosition?.toOffset() ?: Offset.zero
            )
        }

    if (context.isPush) {
        context.fromAnimatable?.animationModifier = drawModifier
    } else {
        context.toAnimatable?.animationModifier = drawModifier
    }*/
}
