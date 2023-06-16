package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.animationElement(key: Any): Modifier = composed {
  with(LocalElementAnimationScope.current) {
    animateEnterExit(
      enter = enterTransitions.getOrElse(key) { EnterTransition.None },
      exit = exitTransitions.getOrElse(key) { ExitTransition.None }
    )
  }
}

val ContentKey = Any()

internal class ElementAnimationScope(
  val scope: AnimatedVisibilityScope,
  val enterTransitions: Map<Any, EnterTransition>,
  val exitTransitions: Map<Any, ExitTransition>
) : AnimatedVisibilityScope by scope

internal val LocalElementAnimationScope = compositionLocalOf<ElementAnimationScope> {
  error("No scope found")
}

interface ElementTransitionBuilder<T> {
  val initial: T?
  val target: T?
  val isPush: Boolean

  infix fun Any.entersWith(transition: EnterTransition)
  infix fun Any.exitsWith(transition: ExitTransition)

  fun containerSizeTransform(sizeTransform: SizeTransform?)
}

internal class ElementTransitionBuilderImpl<T>(
  override val initial: T?,
  override val target: T?,
  override val isPush: Boolean
) : ElementTransitionBuilder<T> {
  val enterTransitions = mutableMapOf<Any, EnterTransition>()
  val exitTransitions = mutableMapOf<Any, ExitTransition>()
  var containerSizeTransform: SizeTransform? = SizeTransform()

  override fun Any.entersWith(transition: EnterTransition) {
    enterTransitions[this] =
      enterTransitions.getOrPut(this) { EnterTransition.None } + transition
  }

  override fun Any.exitsWith(transition: ExitTransition) {
    exitTransitions[this] =
      exitTransitions.getOrPut(this) { ExitTransition.None } + transition
  }

  override fun containerSizeTransform(sizeTransform: SizeTransform?) {
    containerSizeTransform = sizeTransform
  }
}
