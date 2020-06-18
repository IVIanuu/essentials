package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.graphics.Color
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import kotlin.time.Duration
import kotlin.time.milliseconds

fun CircularRevealStackTransition(
    origin: Any,
    color: Color = Color.Unset,
    duration: Duration = 300.milliseconds
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