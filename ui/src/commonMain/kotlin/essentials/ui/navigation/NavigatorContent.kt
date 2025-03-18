/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package essentials.ui.navigation

import android.annotation.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.util.*
import com.slack.circuit.foundation.internal.*
import essentials.*
import essentials.compose.*
import injekt.*
import kotlinx.coroutines.flow.*

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
    RestartableScope {
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
      screenTransition.AnimatedContent(
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
              screenTransition,
              this@SharedTransitionLayout,
              this
            ) { ScreenAnimationScope(this@SharedTransitionLayout, this@AnimatedContent, screenTransition) }
          ) {
            ScreenContent(it)
          }
        }
      }
    }

    RestartableScope {
      val currentOverlay by remember {
        derivedStateOf {
          val currentScreenIndex = navigator.backStack.indexOfLast {
            it !is OverlayScreen<*>
          }

          val overlaysForCurrentScreen = navigator.backStack
            .filterIndexed { i, screen ->
              screen is OverlayScreen<*> &&
                  i > currentScreenIndex
            }

          ScreenAnimationState(
            overlaysForCurrentScreen.lastOrNull(),
            overlaysForCurrentScreen.size
          )
        }
      }

      val overlayTransition = updateTransition(currentOverlay)
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

val LocalScreenAnimationScope = compositionLocalOf<ScreenAnimationScope> {
  error("not provided")
}

data class ScreenAnimationState(
  val current: Screen<*>?,
  val backStackSize: Int
)

val ScreenAnimationScope.isPush: Boolean
  get() = screenTransition.currentState.backStackSize >
      screenTransition.targetState.backStackSize

@Stable class ScreenAnimationScope(
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  val screenTransition: Transition<ScreenAnimationState>
) : SharedTransitionScope by sharedTransitionScope,
  AnimatedVisibilityScope by animatedVisibilityScope

fun interface ScreenTransitionDecorator : ScreenDecorator {
  @Composable override fun DecoratedContent(content: @Composable () -> Unit) {
    val screen = LocalScope.current.screen
    if (screen is OverlayScreen<*>) {
      content()
      return
    }

    Box(modifier = with(LocalScreenAnimationScope.current) { modifier() }) {
      content()
    }
  }

  @SuppressLint("ModifierFactoryExtensionFunction")
  @Composable fun ScreenAnimationScope.modifier(): Modifier

  @Provide companion object {
    @Provide fun <T : ScreenTransitionDecorator> loadingOrder() = LoadingOrder<T>()
      .first()
  }
}

@Provide fun defaultScreenTransitionDecorator(
  screenTransitionDecorator: ScreenTransitionDecorator? = null
): ScreenDecorator = if (screenTransitionDecorator != null) {
  ScreenDecorator { it() }
} else ScreenTransitionDecorator {
  Modifier.animateEnterExit(
    fadeIn() + slideInVertically { it / 2 },
    fadeOut() + slideOutVertically { it / 2 }
  )
}
