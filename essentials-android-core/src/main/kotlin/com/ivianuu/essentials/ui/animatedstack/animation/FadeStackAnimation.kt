package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity
import com.ivianuu.essentials.ui.animatedstack.StackAnimationModifiers
import kotlin.time.Duration
import kotlin.time.milliseconds

fun FadeStackAnimation(
    duration: Duration = 150.milliseconds
) = FloatStackAnimation(duration = duration) { progress ->
    StackAnimationModifiers(
        to = { Modifier.drawOpacity(opacity = progress) },
        from = { Modifier.drawOpacity(opacity = 1f - progress) }
    )
}
