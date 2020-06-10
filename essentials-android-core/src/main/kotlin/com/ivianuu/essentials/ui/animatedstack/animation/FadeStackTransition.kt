package com.ivianuu.essentials.ui.animatedstack.animation

import kotlin.time.Duration
import kotlin.time.milliseconds

fun FadeStackTransition(
    duration: Duration = 150.milliseconds
) = FloatAnimationStackTransition(duration = duration) { fromElement, toElement, _, progress ->
    toElement?.drawLayerModifier?.alpha = progress
    fromElement?.drawLayerModifier?.alpha = 1f - progress
}
