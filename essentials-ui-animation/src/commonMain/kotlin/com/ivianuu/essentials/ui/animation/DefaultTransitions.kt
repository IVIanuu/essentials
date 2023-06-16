package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
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
import androidx.compose.ui.unit.IntOffset

fun ElementTransitionBuilder<*>.fadeThrough(key: Any = ContentKey) {
  key entersWith fadeIn(
    animationSpec = tween(210, delayMillis = 90, easing = LinearOutSlowInEasing)
  ) + scaleIn(
    animationSpec = tween(300, easing = LinearOutSlowInEasing),
    initialScale = if (isPush) 0.8f else 1.1f,
  )

  key exitsWith fadeOut(
    animationSpec = tween(90, easing = FastOutLinearInEasing)
  ) + scaleOut(
    animationSpec = tween(300, easing = FastOutLinearInEasing),
    targetScale = if (isPush) 1.1f else 0.8f
  )
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
        slideOutVertically(tween(300, easing = FastOutLinearInEasing)) { (it * 0.25f).toInt() } +
        fadeOut(tween(300, easing = FastOutLinearInEasing))
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

fun ElementTransitionBuilder<*>.crossFade(
  duration: Int = 300,
  key: Any = ContentKey
) {
  key entersWith fadeIn(tween(duration, easing = LinearOutSlowInEasing))
  key exitsWith fadeOut(tween(duration, easing = FastOutLinearInEasing))
}
