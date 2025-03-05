/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package essentials.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastLastOrNull
import com.slack.circuit.foundation.internal.*
import essentials.*
import essentials.compose.*
import essentials.ui.systembars.LocalZIndex
import injekt.Provide
import kotlinx.coroutines.flow.filter
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance
import kotlin.collections.set

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

      LaunchedEffect(screenState) {
        snapshotFlow { screenState.scope.isDisposed }
          .filter { it }
          .collect { screenStates.remove(screen) }
      }

      key(index) {
        BackHandler(enabled = handleBack && index > 0, onBack = action {
          navigator.pop(screen)
        })
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
            LocalZIndex provides LocalZIndex.current + navigator.backStack.indexOf(screen),
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
            LocalZIndex provides LocalZIndex.current + navigator.backStack.indexOf(screen),
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
  @Composable override fun DecoratedContent(
    content: @Composable (() -> Unit)
  ) {
    val screen = LocalScope.current.screen
    if (screen is OverlayScreen<*> || screen !is OverlayScreen<*>) {
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
    materialSharedAxisXIn(isPush, rememberSlideDistance()),
    materialSharedAxisXOut(isPush, rememberSlideDistance())
  )
}
