package com.ivianuu.essentials.ui.animation

import soup.compose.material.motion.MotionConstants.DefaultMotionDuration
import soup.compose.material.motion.MotionConstants.DefaultSlideDistance
import soup.compose.material.motion.animation.*

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