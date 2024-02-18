package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import soup.compose.material.motion.*
import soup.compose.material.motion.MotionConstants.DefaultFadeInDuration
import soup.compose.material.motion.MotionConstants.DefaultFadeOutDuration
import soup.compose.material.motion.MotionConstants.DefaultMotionDuration
import soup.compose.material.motion.animation.*

fun ElementTransitionBuilder<*>.crossFade(
  durationMillis: Int = DefaultFadeInDuration,
  key: Any = ContentKey
) {
  key entersWith simpleFadeIn(durationMillis)
  key exitsWith simpleFadeOut(durationMillis)
}

fun simpleFadeIn(durationMillis: Int = DefaultFadeInDuration) = fadeIn(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = LinearEasing
  )
)

fun simpleFadeOut(durationMillis: Int = DefaultFadeOutDuration) = fadeOut(
  animationSpec = tween(
    durationMillis = durationMillis,
    easing = LinearEasing
  )
)

fun ElementTransitionBuilder<*>.fadeUpwards(
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  if (isPush) {
    key entersWith
        slideInVertically(tween(durationMillis, easing = FastOutSlowInEasing)) { (it * 0.25f).toInt() } +
        fadeIn(tween(durationMillis.ForIncoming, durationMillis.ForOutgoing, easing = LinearOutSlowInEasing))
    key exitsWith holdOut(300)
  } else {
    key exitsWith
        slideOutVertically(tween(durationMillis, easing = FastOutLinearInEasing)) { (it * 0.25f).toInt() } +
        fadeOut(tween(durationMillis.ForOutgoing, easing = FastOutLinearInEasing))
  }
}

fun ElementTransitionBuilder<*>.slideHorizontally(
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  if (isPush) {
    key entersWith slideInHorizontally(tween(durationMillis)) { it }
    key exitsWith slideOutHorizontally(tween(durationMillis)) { -it }
  } else {
    key entersWith slideInHorizontally(tween(durationMillis)) { -it }
    key exitsWith slideOutHorizontally(tween(durationMillis)) { it }
  }
}

fun ElementTransitionBuilder<*>.materialFade(
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  key entersWith materialFadeIn(durationMillis)
  key exitsWith materialFadeOut(durationMillis)
}

fun ElementTransitionBuilder<*>.materialFadeThrough(
  durationMillis: Int = DefaultMotionDuration,
  key: Any = ContentKey
) {
  key entersWith materialFadeThroughIn(durationMillis = durationMillis)
  key exitsWith materialFadeThroughOut(durationMillis = durationMillis)
}

fun ElementTransitionBuilder<*>.materialSharedAxisX(
  slideDistance: Int = MotionConstants.DefaultSlideDistance.roundToPx(),
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

fun ElementTransitionBuilder<*>.materialSharedAxisY(
  slideDistance: Int = MotionConstants.DefaultSlideDistance.roundToPx(),
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

private const val ProgressThreshold = 0.35f
val Int.ForOutgoing: Int get() = (this * ProgressThreshold).toInt()
val Int.ForIncoming: Int get() = this - this.ForOutgoing
private const val DefaultFadeEndThresholdEnter = 0.3f
val Int.ForFade: Int
  get() = (this * DefaultFadeEndThresholdEnter).toInt()
