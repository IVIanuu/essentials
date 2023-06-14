@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER", "INVISIBLE_SETTER")

package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier

@Composable fun AnimatedVisibility(
  visible: Boolean,
  modifier: Modifier = Modifier,
  transitionSpec: AnimationScope<Boolean>.() -> ContentTransform = {
    expandHorizontally() +
        fadeIn() with shrinkHorizontally() + fadeOut()
  },
  content: @Composable AnimatedVisibilityScope.() -> Unit
) {
  AnimatedContent(visible, transitionSpec = transitionSpec) {
    if (it)
      Box(modifier, propagateMinConstraints = true) { content() }
  }
}

@Composable fun <T> AnimatedContent(
  state: T,
  modifier: Modifier = Modifier,
  transitionSpec: AnimationScope<T>.() -> ContentTransform = {
    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) with
        fadeOut(animationSpec = tween(90))
  },
  contentKey: (T) -> Any? = { it },
  content: @Composable AnimatedVisibilityScope.(T) -> Unit
) {
  AnimatedStack(
    listOf(state),
    modifier,
    transitionSpec,
    contentKey,
    content = content
  )
}

@Composable fun <T> AnimatedStack(
  items: List<T>,
  modifier: Modifier = Modifier,
  transitionSpec: AnimationScope<T>.() -> ContentTransform = {
    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) with
        fadeOut(animationSpec = tween(90))
  },
  contentKey: (T) -> Any? = { it },
  contentOpaque: (T) -> Boolean = { false },
  content: @Composable AnimatedVisibilityScope.(T) -> Unit
) {
  val transition = updateTransition(targetState = items)

  fun List<T>.filterVisible(): List<T> = buildList {
    for (stateForContent in this@filterVisible.reversed()) {
      add(0, stateForContent)
      if (!contentOpaque(stateForContent)) break
    }
  }

  val scope = remember(transition) { AnimationScope(transition) }

  val currentlyVisible = remember(transition) {
    transition.currentState.filterVisible().toMutableStateList()
  }

  items.filterVisible().forEach {
    if (it !in currentlyVisible)
      currentlyVisible.add(it)
  }

  Box(modifier = modifier) {
    currentlyVisible.forEach { stateForContent ->
      key(contentKey(stateForContent)) {
        val specOnEnter = remember { transitionSpec(scope) }
        val exit = remember(stateForContent in transition.segment.targetState) {
          if (stateForContent in transition.segment.targetState) {
            ExitTransition.None
          } else {
            scope.transitionSpec().initialContentExit
          }
        }

        transition.AnimatedVisibility(
          visible = { stateForContent in it.filterVisible() },
          enter = specOnEnter.targetContentEnter,
          exit = exit
        ) {
          DisposableEffect(this) {
            onDispose {
              currentlyVisible.remove(stateForContent)
            }
          }
          content(stateForContent)
        }
      }
    }
  }
}

class AnimationScope<T>(
  private val transition: Transition<List<T>>
) : Transition.Segment<List<T>> {
  override val initialState: List<T>
    @Suppress("UnknownNullness")
    get() = transition.segment.initialState

  override val targetState: List<T>
    @Suppress("UnknownNullness")
    get() = transition.segment.targetState
}
