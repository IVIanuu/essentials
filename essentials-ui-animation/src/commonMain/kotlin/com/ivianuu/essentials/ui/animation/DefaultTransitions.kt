package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset

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

fun ElementTransitionBuilder<*>.fadeUpwards(key: Any = ContentKey) {
  if (isPush) {
    key entersWith
        slideInVertically(tween(300, easing = FastOutSlowInEasing)) { (it * 0.25f).toInt() } +
        fadeIn(tween(300, easing = FastOutLinearInEasing))
    // make sure that the existing screen stays for the duration of the transition
    key exitsWith fadeOut(tween(300), 0.99f)
  } else {
    key exitsWith
        slideOutVertically(tween(300, easing = FastOutSlowInEasing)) { (it * 0.25f).toInt() } +
        fadeOut(tween(300, easing = FastOutLinearInEasing))
  }
}

fun ElementTransitionBuilder<*>.crossFade(
  animationSpec: FiniteAnimationSpec<Float> = tween(300),
  key: Any = ContentKey
) {
  key entersWith fadeIn(animationSpec)
  key exitsWith fadeOut(animationSpec)
}
