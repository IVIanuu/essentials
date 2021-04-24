package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.animation.*
import kotlin.math.*
import kotlin.time.*

fun CircularRevealStackTransition(
    centerElementKey: Any,
    spec: AnimationSpec<Float> = defaultAnimationSpec(500.milliseconds, easing = FastOutSlowInEasing)
): StackTransition = {
    val clipTarget = if (isPush) toElementModifier(ContentAnimationElementKey)
    else fromElementModifier(ContentAnimationElementKey)
    if (isPush) clipTarget?.value = Modifier.alpha(0f)
    attachTo()

    val center: Offset
    val startRadius: Float
    val endRadius: Float
    if (isPush) {
        val toElementCoords = toElementModifier(ContentAnimationElementKey)!!.awaitLayoutCoordinates()
        val centerElementCoords = fromElementModifier(centerElementKey)!!.awaitLayoutCoordinates()
        center = centerElementCoords.boundsInWindow().center
        startRadius = 0f
        endRadius = hypot(
            toElementCoords.size.width.toFloat(),
            toElementCoords.size.height.toFloat()
        )
    } else {
        val toElementCoords = fromElementModifier(ContentAnimationElementKey)!!.awaitLayoutCoordinates()
        val centerElementCoords = toElementModifier(centerElementKey)!!.awaitLayoutCoordinates()
        center = centerElementCoords.boundsInWindow().center
        startRadius = hypot(
            toElementCoords.size.width.toFloat(),
            toElementCoords.size.height.toFloat()
        )
        endRadius = 0f
    }

    val path = Path()
    animate(spec) { value ->
        clipTarget?.value = Modifier
            .drawWithContent {
                clipPath(
                    path = path.apply {
                        reset()
                        addOval(
                            Rect(
                                center,
                                lerp(startRadius, endRadius, value)
                            )
                        )
                    }
                ) {
                    this@drawWithContent.drawContent()
                }
            }
    }
}
