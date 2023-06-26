package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

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
        fadeIn(tween(durationMillis.forIncomingSharedAxis, durationMillis.forOutgoingSharedAxis, easing = LinearOutSlowInEasing))
    key exitsWith holdOut(300)
  } else {
    key exitsWith
        slideOutVertically(tween(durationMillis, easing = FastOutLinearInEasing)) { (it * 0.25f).toInt() } +
        fadeOut(tween(durationMillis.forOutgoingSharedAxis, easing = FastOutLinearInEasing))
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
