package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.TransformOrigin
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import kotlin.time.Duration
import kotlin.time.milliseconds

fun ScaleStackAnimation(duration: Duration = 300.milliseconds): StackAnimation {
    return FloatStackAnimation(duration = duration) { fromElement, toElement, isPush, progress ->
        if (toElement != null && isPush) {
            toElement.drawLayerModifier.scaleX = progress
            toElement.drawLayerModifier.scaleY = progress
            toElement.drawLayerModifier.transformOrigin = TransformOrigin(0.5f, 0.5f)
        }

        if (fromElement != null && !isPush) {
            fromElement.drawLayerModifier.scaleX = 1f - progress
            fromElement.drawLayerModifier.scaleY = 1f - progress
            fromElement.drawLayerModifier.transformOrigin = TransformOrigin(0.5f, 0.5f)
        }
    }
}
