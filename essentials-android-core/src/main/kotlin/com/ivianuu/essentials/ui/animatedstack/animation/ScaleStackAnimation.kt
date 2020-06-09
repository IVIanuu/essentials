package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.Modifier
import androidx.ui.core.TransformOrigin
import androidx.ui.core.drawLayer
import com.ivianuu.essentials.ui.animatedstack.StackAnimation
import com.ivianuu.essentials.ui.animatedstack.StackAnimationModifiers
import kotlin.time.Duration
import kotlin.time.milliseconds

fun ScaleStackAnimation(duration: Duration = 300.milliseconds): StackAnimation {
    return FloatStackAnimation(duration = duration) { _, _, isPush, progress ->
        StackAnimationModifiers(
            to = {
                Modifier.drawLayer(
                    scaleX = progress, scaleY = progress,
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                )
            },
            from = {
                Modifier.drawLayer(
                    scaleX = 1f - progress, scaleY = 1f - progress,
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                )
            }
        )
    }
}
