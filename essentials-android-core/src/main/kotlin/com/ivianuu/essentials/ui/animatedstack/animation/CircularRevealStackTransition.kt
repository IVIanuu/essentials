package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.TweenBuilder
import androidx.compose.onActive
import androidx.ui.animation.animatedFloat
import androidx.ui.core.Modifier
import androidx.ui.core.drawWithContent
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.useOrElse
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.center
import androidx.ui.unit.toOffset
import androidx.ui.util.lerp
import com.ivianuu.essentials.ui.animatable.AnimatableElementsAmbient
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import kotlin.math.hypot
import kotlin.time.Duration
import kotlin.time.milliseconds

fun CircularRevealStackTransition(
    origin: Any,
    color: Color = Color.Unset,
    duration: Duration = 300.milliseconds
): StackTransition = { context ->
    if (context.toElement != null) onActive { context.addTo() }

    val originElement = AnimatableElementsAmbient.current.getElement(origin).value
    val capturedOriginBounds = originElement.capturedBounds()

    val centerPosition = capturedOriginBounds?.center()
    val startRadius = if (context.isPush) 0f else centerPosition?.let { hypot(it.x, it.y) } ?: 0f
    val endRadius = if (context.isPush) centerPosition?.let { hypot(it.x, it.y) } ?: 0f else 0f

    val animation = animatedFloat(0f)

    context.toElement?.drawLayerModifier?.alpha = animation.value
    context.fromElement?.drawLayerModifier?.alpha = 1f - animation.value

    if (capturedOriginBounds != null) {
        onActive {
            animation.animateTo(
                targetValue = 1f,
                anim = TweenBuilder<Float>().apply {
                    this.duration = duration.toLongMilliseconds().toInt()
                },
                onEnd = { _, _ ->
                    if (context.fromElement != null) context.removeFrom()
                    context.fromElement?.animationModifier = Modifier
                    context.toElement?.animationModifier = Modifier
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
        context.fromElement?.animationModifier = drawModifier
    } else {
        context.toElement?.animationModifier = drawModifier
    }
}
