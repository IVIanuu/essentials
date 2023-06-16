package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp

fun holdIn(durationMillis: Int = DefaultMotionDuration) = fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = LinearEasing
  ),
  initialAlpha = 1f,
)

fun holdOut(durationMillis: Int = DefaultMotionDuration) = fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = LinearEasing
  ),
  targetAlpha = 0.999f
)

private const val DefaultFadeEndThresholdEnter = 0.3f

val Int.forMaterialFade: Int
  get() = (this * DefaultFadeEndThresholdEnter).toInt()

fun ElementTransitionBuilder<*>.materialFade(
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  key entersWith materialFadeIn(durationMillis)
  key exitsWith materialFadeOut(durationMillis)
}

fun materialFadeIn(
  durationMillis: Int = DefaultFadeInDuration,
  transformOrigin: TransformOrigin = TransformOrigin.Center
) = fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis.forMaterialFade,
    easing = LinearEasing
  )
) + scaleIn(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  initialScale = 0.8f,
  transformOrigin = transformOrigin
)

fun materialFadeOut(durationMillis: Int = DefaultFadeOutDuration) = fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = LinearEasing
  )
)

private const val FadeThroughProgressThreshold = 0.35f

val Int.forOutgoingFadeThrough: Int
  get() = (this * FadeThroughProgressThreshold).toInt()

val Int.forIncomingFadeThrough: Int
  get() = this - forOutgoingFadeThrough

fun ElementTransitionBuilder<*>.materialFadeThrough(
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  key entersWith materialFadeThroughIn(durationMillis = durationMillis);
  key exitsWith materialFadeThroughOut(durationMillis = durationMillis)
}

fun materialFadeThroughIn(
  initialScale: Float = 0.92f,
  durationMillis: Int = DefaultMotionDuration,
) = fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis.forIncomingFadeThrough,
    delayMillis = durationMillis.forOutgoingFadeThrough,
    easing = LinearOutSlowInEasing
  )
) + scaleIn(
  animationSpec = tween(
    durationMillis = durationMillis.forIncomingFadeThrough,
    delayMillis = durationMillis.forOutgoingFadeThrough,
    easing = LinearOutSlowInEasing
  ),
  initialScale = initialScale
)

fun materialFadeThroughOut(
  durationMillis: Int = DefaultMotionDuration,
) = fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis.forOutgoingFadeThrough,
    delayMillis = 0,
    easing = FastOutLinearInEasing
  )
)

private const val SharedAxisProgressThreshold = 0.35f

val Int.forOutgoingSharedAxis: Int
  get() = (this * SharedAxisProgressThreshold).toInt()

val Int.forIncomingSharedAxis: Int
  get() = this - this.forOutgoingSharedAxis

fun ElementTransitionBuilder<*>.materialSharedAxisX(
  slideDistance: Int = DefaultSlideDistance.roundToPx(),
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  key entersWith materialSharedAxisXIn(
    forward = isPush,
    slideDistance = slideDistance,
    durationMillis = durationMillis
  )
  key exitsWith materialSharedAxisXOut(
    forward = isPush,
    slideDistance = slideDistance,
    durationMillis = durationMillis
  )
}

fun materialSharedAxisXIn(
  forward: Boolean,
  slideDistance: Int,
  durationMillis: Int = DefaultMotionDuration,
) = slideInHorizontally(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  initialOffsetX = { if (forward) slideDistance else -slideDistance }
) + fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis.forIncomingSharedAxis,
    delayMillis = durationMillis.forOutgoingSharedAxis,
    easing = LinearOutSlowInEasing
  )
)

fun materialSharedAxisXOut(
  forward: Boolean,
  slideDistance: Int,
  durationMillis: Int = DefaultMotionDuration,
) = slideOutHorizontally(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  targetOffsetX = { if (forward) -slideDistance else slideDistance }
) + fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis.forOutgoingSharedAxis,
    delayMillis = 0,
    easing = FastOutLinearInEasing
  )
)

fun ElementTransitionBuilder<*>.materialSharedAxisY(
  slideDistance: Int = DefaultSlideDistance.roundToPx(),
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  key entersWith materialSharedAxisYIn(
    forward = isPush,
    slideDistance = slideDistance,
    durationMillis = durationMillis
  )
  key exitsWith materialSharedAxisYOut(
    forward = isPush,
    slideDistance = slideDistance,
    durationMillis = durationMillis
  )
}

fun materialSharedAxisYIn(
  forward: Boolean,
  slideDistance: Int,
  durationMillis: Int = DefaultMotionDuration,
) = slideInVertically(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  initialOffsetY = { if (forward) slideDistance else -slideDistance }
) + fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis.forIncomingSharedAxis,
    delayMillis = durationMillis.forOutgoingSharedAxis,
    easing = LinearOutSlowInEasing
  )
)

fun materialSharedAxisYOut(
  forward: Boolean,
  slideDistance: Int,
  durationMillis: Int = DefaultMotionDuration,
) = slideOutVertically(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  targetOffsetY = { if (forward) -slideDistance else slideDistance }
) + fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis.forOutgoingSharedAxis,
    delayMillis = 0,
    easing = FastOutLinearInEasing
  )
)

fun ElementTransitionBuilder<*>.materialSharedAxisZ(
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  key entersWith materialSharedAxisZIn(
    forward = isPush,
    durationMillis = durationMillis
  )
  key exitsWith materialSharedAxisZOut(
    forward = isPush,
    durationMillis = durationMillis
  )
}

fun materialSharedAxisZIn(
  forward: Boolean,
  durationMillis: Int = DefaultMotionDuration,
) = fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis.forIncomingSharedAxis,
    delayMillis = durationMillis.forOutgoingSharedAxis,
    easing = LinearOutSlowInEasing
  )
) + scaleIn(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  initialScale = if (forward) 0.8f else 1.1f
)

fun materialSharedAxisZOut(
  forward: Boolean,
  durationMillis: Int = DefaultMotionDuration,
) = fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis.forOutgoingSharedAxis,
    delayMillis = 0,
    easing = FastOutLinearInEasing
  )
) + scaleOut(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = FastOutSlowInEasing
  ),
  targetScale = if (forward) 1.1f else 0.8f
)

const val DefaultMotionDuration = 300
const val DefaultFadeInDuration = 150
const val DefaultFadeOutDuration = 75
val DefaultSlideDistance = 30.dp
