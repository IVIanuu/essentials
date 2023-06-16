package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset

fun ElementTransitionBuilder<*>.crossFade(key: Any = ContentKey) {
  key entersWith simpleFadeIn()
  key exitsWith simpleFadeOut()
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

fun ElementTransitionBuilder<*>.fadeUpwards(key: Any = ContentKey) {
  if (isPush) {
    key entersWith
        slideInVertically(tween(300, easing = FastOutSlowInEasing)) { (it * 0.25f).toInt() } +
        fadeIn(tween(300.forIncomingSharedAxis, 300.forOutgoingSharedAxis, easing = LinearOutSlowInEasing))
    key exitsWith holdOut(300)
  } else {
    key exitsWith
        slideOutVertically(tween(300, easing = FastOutLinearInEasing)) { (it * 0.25f).toInt() } +
        fadeOut(tween(300.forOutgoingSharedAxis, easing = FastOutLinearInEasing))
  }
}

fun ElementTransitionBuilder<*>.slideHorizontally(
  animationSpec: FiniteAnimationSpec<IntOffset> = tween(300),
  key: Any = ContentKey
) {
  if (isPush) {
    key entersWith slideInHorizontally(animationSpec) { it }
    key exitsWith slideOutHorizontally(animationSpec) { -it }
  } else {
    key entersWith slideInHorizontally(animationSpec) { -it }
    key exitsWith slideOutHorizontally(animationSpec) { it }
  }
}
