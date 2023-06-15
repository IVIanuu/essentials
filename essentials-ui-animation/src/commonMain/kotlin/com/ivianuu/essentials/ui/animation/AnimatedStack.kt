@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER", "INVISIBLE_SETTER")

package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier

@Composable fun AnimatedVisibility(
  visible: Boolean,
  modifier: Modifier = Modifier,
  transitionSpec: ElementTransitionSpec<Boolean> = {
    ContentKey entersWith expandHorizontally() + fadeIn()

    ContentKey exitsWith shrinkHorizontally() + fadeOut()
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
  transitionSpec: ElementTransitionSpec<T> = {
    ContentKey entersWith
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))

    ContentKey exitsWith fadeOut(animationSpec = tween(90))
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
  transitionSpec: ElementTransitionSpec<T> = {
    ContentKey entersWith
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))

    ContentKey exitsWith fadeOut(animationSpec = tween(300))
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
  
  val currentlyVisible = remember(transition) {
    transition.currentState.filterVisible().toMutableStateList()
  }

  items.filterVisible().forEach {
    if (it !in currentlyVisible)
      currentlyVisible.add(it)
  }

  val enterTransitions = remember { mutableStateMapOf<T, MutableMap<Any, EnterTransition>>() }
  val exitTransitions = remember { mutableStateMapOf<T, MutableMap<Any, ExitTransition>>() }

  fun updateTransitions(initial: T?, target: T?, isPush: Boolean) {
    ElementTransitionBuilderImpl(
      initial,
      target,
      isPush
    ).apply(transitionSpec)
      .let { builder ->
        if (target != null)
          enterTransitions[target] = builder.enterTransitions
        if (initial != null)
          exitTransitions[initial] = builder.exitTransitions
      }
  }

  if (transition.targetState != transition.currentState) {
    remember(transition.targetState, transition.currentState) {
      val initialVisibleItems = transition.currentState.filterVisible()

      val targetVisibleItems = transition.targetState.filterVisible()

      if (initialVisibleItems != targetVisibleItems) {
        val initialTopItem = initialVisibleItems.lastOrNull()
        val targetTopItem = targetVisibleItems.lastOrNull()

        // check if we should animate the top items
        val replacingTopItems = targetTopItem != null && initialTopItem != targetTopItem &&
            targetVisibleItems.count { it !in initialVisibleItems } == 1

        // Remove all visible items which shouldn't be visible anymore
        // from top to bottom
        initialVisibleItems
          .dropLast(if (replacingTopItems) 1 else 0)
          .reversed()
          .filterNot { it in targetVisibleItems }
          .forEach { updateTransitions(it, null, false) }

        // Add any new items to the stack from bottom to top
        targetVisibleItems
          .dropLast(if (replacingTopItems) 1 else 0)
          .filterNot { it in initialVisibleItems }
          .forEachIndexed { i, item ->
            updateTransitions(
              targetVisibleItems.getOrNull(i - 1),
              item,
              true
            )
          }


        // Replace the old visible top with the new one
        if (replacingTopItems)
          updateTransitions(
            initialTopItem,
            targetTopItem,
            targetTopItem !in transition.currentState
          )
      }
    }
  }

  Box(modifier = modifier) {
    currentlyVisible.forEach { stateForContent ->
      key(contentKey(stateForContent)) {
        val enterTransitionsForState = enterTransitions[stateForContent] ?: emptyMap()
        val exitTransitionsForState = exitTransitions[stateForContent] ?: emptyMap()

        transition.AnimatedVisibility(
          visible = { stateForContent in it.filterVisible() },
          enter = enterTransitionsForState[ContentKey] ?: EnterTransition.None,
          exit = exitTransitionsForState[ContentKey] ?: ExitTransition.None
        ) {
          DisposableEffect(this) {
            onDispose {
              currentlyVisible.remove(stateForContent)
              enterTransitions.remove(stateForContent)
              exitTransitions.remove(stateForContent)
            }
          }

          CompositionLocalProvider(
            LocalElementAnimationScope provides remember(this, enterTransitionsForState, exitTransitionsForState) {
              ElementAnimationScope(
                this,
                enterTransitionsForState,
                exitTransitionsForState
              )
            }
          ) {
            content(stateForContent)
          }
        }
      }
    }
  }
}

typealias ElementTransitionSpec<T> = ElementTransitionBuilder<T>.() -> Unit
