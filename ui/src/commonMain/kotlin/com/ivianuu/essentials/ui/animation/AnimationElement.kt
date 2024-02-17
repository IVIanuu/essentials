package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Density

fun Modifier.animationElement(key: Any): Modifier = composed {
  with(LocalElementAnimationScope.current) {
    animateEnterExit(
      enter = enterTransitions.getOrElse(key) { EnterTransition.None },
      exit = exitTransitions.getOrElse(key) { ExitTransition.None }
    )
  }
}

val ContentKey = "content"

internal class ElementAnimationScope(
  val scope: AnimatedVisibilityScope,
  val enterTransitions: Map<Any, EnterTransition>,
  val exitTransitions: Map<Any, ExitTransition>
) : AnimatedVisibilityScope by scope

internal val LocalElementAnimationScope = compositionLocalOf<ElementAnimationScope> {
  error("No scope found")
}

class ElementTransitionBuilder<T>(
  val initial: T?,
  val target: T?,
  val isPush: Boolean,
  density: Density
) : Density by density {
  val enterTransitions = mutableMapOf<Any, EnterTransition>()
  val exitTransitions = mutableMapOf<Any, ExitTransition>()
  var containerSizeTransform: SizeTransform? = SizeTransform()

  infix fun Any.entersWith(transition: EnterTransition) {
    enterTransitions[this] =
      enterTransitions.getOrPut(this) { EnterTransition.None } + transition
  }

  infix fun Any.exitsWith(transition: ExitTransition) {
    exitTransitions[this] =
      exitTransitions.getOrPut(this) { ExitTransition.None } + transition
  }

  fun containerSizeTransform(sizeTransform: SizeTransform?) {
    containerSizeTransform = sizeTransform
  }
}
