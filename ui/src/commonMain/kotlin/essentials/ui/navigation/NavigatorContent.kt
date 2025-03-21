/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package essentials.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.util.*
import com.slack.circuit.foundation.internal.*
import essentials.*
import essentials.compose.*
import kotlinx.coroutines.flow.*
import soup.compose.material.motion.animation.*

@Composable fun NavigatorContent(
  navigator: Navigator,
  modifier: Modifier = Modifier,
  handleBack: Boolean = true
) {
  NavigatorContent<RootNavGraph>(
    navigator,
    LocalScope.current.service(),
    modifier,
    handleBack
  )
}

@Composable fun <N> NavigatorContent(
  navigator: Navigator,
  navigationComponent: NavigationComponent<N>,
  modifier: Modifier = Modifier,
  handleBack: Boolean = true
) {
  val screenStates = remember {
    mutableStateMapOf<Screen<*>, ScreenState<*>>()
  }

  navigator.backStack.fastForEachIndexed { index, screen ->
    key(screen) {
      val screenState = rememberScreenState(screen, navigator, navigationComponent)
      screenStates[screen] = screenState

      key(index) {
        BackHandler(enabled = handleBack && index > 0, onBack = action {
          navigator.pop(screen)
        })
      }
    }
  }

  screenStates.forEach { (screen, screenState) ->
    key(screen) {
      LaunchedEffect(screenState) {
        snapshotFlow { screenState.scope.isDisposed }
          .filter { it }
          .collect { screenStates.remove(screen) }
      }
    }
  }

  SharedTransitionLayout(modifier = modifier) {
    val currentScreenState by remember {
      derivedStateOf {
        ScreenAnimationState(
          navigator.backStack.fastLastOrNull {
            it !is OverlayScreen
          },
          navigator.backStack.count { it !is OverlayScreen<*> }
        )
      }
    }

    val screenTransition = updateTransition(
      targetState = currentScreenState
    )
    val slideDistance = rememberSlideDistance()
    screenTransition.AnimatedContent(
      modifier = Modifier.fillMaxSize(),
      transitionSpec = {
        val isPush = targetState.backStackSize > initialState.backStackSize
        materialSharedAxisXIn(isPush, slideDistance) togetherWith
            materialSharedAxisXOut(isPush, slideDistance)
      }
    ) { (screen) ->
      screenStates[screen]?.let {
        CompositionLocalProvider(
          LocalScreenAnimationScope provides remember(
            screenTransition,
            this@SharedTransitionLayout,
            this
          ) { ScreenAnimationScope(this@SharedTransitionLayout, this@AnimatedContent, screenTransition) }
        ) {
          Box {
            ScreenContent(it)

            val currentOverlayState by remember {
              derivedStateOf {
                val overlays = mutableListOf<OverlayScreen<*>>()

                val currentScreenIndex = navigator.backStack.indexOf(screen)
                if (currentScreenIndex >= 0 && currentScreenIndex != navigator.backStack.lastIndex)
                  for (i in currentScreenIndex + 1 until navigator.backStack.size) {
                    val innerScreen = navigator.backStack[i]
                    if (innerScreen !is OverlayScreen<*>) break
                    overlays.add(innerScreen)
                  }

                ScreenAnimationState(overlays.lastOrNull(), overlays.size)
              }
            }

            val overlayTransitionState = rememberSaveable {
              MutableTransitionState(currentOverlayState)
            }
            val overlayTransition = rememberTransition(overlayTransitionState)
            overlayTransitionState.targetState = currentOverlayState

            overlayTransition.AnimatedContent(
              modifier = Modifier.fillMaxSize(),
              transitionSpec = {
                ContentTransform(
                  EnterTransition.None,
                  ExitTransition.None,
                  sizeTransform = null
                )
              }
            ) { (screen) ->
              screenStates[screen]?.let {
                CompositionLocalProvider(
                  LocalScreenAnimationScope provides remember(
                    overlayTransition,
                    this@SharedTransitionLayout,
                    this
                  ) {
                    ScreenAnimationScope(
                      this@SharedTransitionLayout,
                      this@AnimatedContent,
                      overlayTransition
                    )
                  }
                ) {
                  ScreenContent(it)
                }
              }
            }
          }
        }
      }
    }
  }
}

val LocalScreenAnimationScope = compositionLocalOf<ScreenAnimationScope> {
  error("not provided")
}

data class ScreenAnimationState(val current: Screen<*>?, val backStackSize: Int)

val ScreenAnimationScope.isPush: Boolean
  get() = screenTransition.currentState.backStackSize >
      screenTransition.targetState.backStackSize

@Stable class ScreenAnimationScope(
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  val screenTransition: Transition<ScreenAnimationState>
) : SharedTransitionScope by sharedTransitionScope,
  AnimatedVisibilityScope by animatedVisibilityScope
