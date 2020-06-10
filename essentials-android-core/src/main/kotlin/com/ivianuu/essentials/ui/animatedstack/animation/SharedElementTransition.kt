package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.TweenBuilder
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.ui.animation.animatedFloat
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.drawLayer
import androidx.ui.foundation.Box
import androidx.ui.layout.offset
import androidx.ui.layout.preferredSize
import androidx.ui.unit.PxPosition
import androidx.ui.unit.height
import androidx.ui.unit.lerp
import androidx.ui.unit.width
import androidx.ui.util.lerp
import com.ivianuu.essentials.ui.animatable.AnimatableElementPropKey
import com.ivianuu.essentials.ui.animatable.AnimatableElementsAmbient
import com.ivianuu.essentials.ui.animatable.animationOverlay
import com.ivianuu.essentials.ui.animatedstack.StackTransition

val SharedElementKey = AnimatableElementPropKey<@Composable () -> Unit>()

fun SharedElementStackTransition(
    fromTag: Any,
    toTag: Any
): StackTransition = { context ->
    onActive { context.addTo() }

    val startElement = AnimatableElementsAmbient.current.getElement(
        if (context.isPush) fromTag else toTag
    ).value
    val endElement = AnimatableElementsAmbient.current.getElement(
        if (context.isPush) toTag else fromTag
    ).value

    val capturedStartBounds = startElement.capturedBounds()
    val capturedEndBounds = endElement.capturedBounds()

    val animation = animatedFloat(0f)

    // animate the root elements
    context.toElement?.drawLayerModifier?.alpha = animation.value
    context.fromElement?.drawLayerModifier?.alpha = 1f - animation.value

    if (capturedStartBounds != null && capturedEndBounds != null) {
        onActive {
            // hide the "real" elements
            startElement.drawLayerModifier.alpha = 0f
            endElement.drawLayerModifier.alpha = 0f

            animation.animateTo(
                targetValue = 1f,
                onEnd = { _, _ ->
                    context.removeFrom()
                    startElement.drawLayerModifier.alpha = 1f
                    endElement.drawLayerModifier.alpha = 1f
                    context.onComplete()
                },
                anim = TweenBuilder<Float>().apply {
                    duration = 300
                }
            )
        }
    }

    val startProps = if (capturedStartBounds != null && capturedEndBounds != null) {
        SharedElementProps(
            position = PxPosition(
                x = capturedStartBounds.left + (capturedStartBounds.width - capturedEndBounds.width) / 2,
                y = capturedStartBounds.top + (capturedStartBounds.height - capturedEndBounds.height) / 2
            ),
            scaleX = capturedStartBounds.width / capturedEndBounds.width,
            scaleY = capturedStartBounds.height / capturedEndBounds.height
        )
    } else null

    val endProps = if (capturedEndBounds != null) {
        SharedElementProps(
            position = PxPosition(capturedEndBounds.left, capturedEndBounds.top),
            scaleX = 1f,
            scaleY = 1f
        )
    } else null

    val currentProps = if (startProps != null && endProps != null) {
        SharedElementProps(
            position = lerp(startProps.position, endProps.position, animation.value),
            scaleX = lerp(startProps.scaleX, endProps.scaleX, animation.value),
            scaleY = lerp(startProps.scaleY, endProps.scaleY, animation.value)
        )
    } else null

    if (currentProps != null) {
        animationOverlay {
            val sharedElementComposable = endElement.properties.getOrNull(SharedElementKey)
            with(DensityAmbient.current) {
                Box(
                    modifier = Modifier
                        .preferredSize(
                            width = capturedEndBounds!!.width.toDp(),
                            height = capturedEndBounds.height.toDp()
                        )
                        .offset(
                            x = currentProps.position.x.toDp(),
                            y = currentProps.position.y.toDp()
                        )
                        .drawLayer(scaleX = currentProps.scaleX, scaleY = currentProps.scaleY),
                    children = sharedElementComposable ?: {}
                )
            }
        }
    }
}

data class SharedElementProps(
    val position: PxPosition,
    val scaleX: Float,
    val scaleY: Float
)
