/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.util.fastForEachIndexed
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.systembars.LocalZIndex
import com.slack.circuit.foundation.internal.*
import kotlin.collections.set

@Composable fun NavigatorContent(
  navigator: Navigator,
  modifier: Modifier = Modifier,
  handleBack: Boolean = true,
  defaultTransitionSpec: ElementTransitionSpec<Screen<*>> = LocalScreenTransitionSpec.current
) {
  NavigatorContent<RootNavGraph>(
    navigator,
    LocalScope.current.service(),
    modifier,
    handleBack,
    defaultTransitionSpec
  )
}

@Composable fun <N> NavigatorContent(
  navigator: Navigator,
  navigationComponent: NavigationComponent<N>,
  modifier: Modifier = Modifier,
  handleBack: Boolean = true,
  defaultTransitionSpec: ElementTransitionSpec<Screen<*>> = LocalScreenTransitionSpec.current
) {
  val screenStates = remember {
    mutableStateMapOf<Screen<*>, ScreenState<*>>()
  }

  navigator.backStack.fastForEachIndexed { index, screen ->
    key(screen) {
      screenStates[screen] = rememberScreenState(screen, navigator, navigationComponent)

      key(index) {
        BackHandler(enabled = handleBack && index > 0, onBack = action {
          navigator.pop(screen)
        })
      }
    }
  }

  // clean up removed states
  screenStates.forEach { (screen, screenState) ->
    key(screenState) {
      DisposableEffect(true) {
        screenState.scope.addObserver(
          object : ScopeObserver<Any> {
            override fun onExit(scope: Scope<Any>) {
              screenStates.remove(screen)
            }
          }
        )
        onDispose {  }
      }
    }
  }

  AnimatedStack(
    modifier = modifier,
    items = navigator.backStack,
    contentOpaque = { screenStates[it]?.config?.opaque == true },
    transitionSpec = {
      val spec = (if (isPush)
        screenStates[target]?.config?.enterTransitionSpec
      else
        screenStates[initial]?.config?.exitTransitionSpec)
        ?: defaultTransitionSpec

      with(spec) { invoke() }
    }
  ) { screen ->
    screenStates[screen]?.let {
      CompositionLocalProvider(
        LocalZIndex provides LocalZIndex.current + navigator.backStack.indexOf(screen)
      ) {
        ScreenContent(it)
      }
    }
  }
}

val LocalScreenTransitionSpec = compositionLocalOf<ElementTransitionSpec<Screen<*>>> {
  ElementTransitionSpec {  }
}
