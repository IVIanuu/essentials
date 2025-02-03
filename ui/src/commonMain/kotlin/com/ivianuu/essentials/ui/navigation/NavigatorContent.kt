/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.*
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
  screenContextComponent: ScreenContextComponent<N>,
  modifier: Modifier = Modifier,
  handleBack: Boolean = true,
  defaultTransitionSpec: ElementTransitionSpec<Screen<*>> = LocalScreenTransitionSpec.current
) {
  val screenContexts = remember { mutableStateMapOf<Screen<*>, ScreenContext<*>>() }

  navigator.backStack.forEachIndexed { index, screen ->
    key(screen) {
      screenContexts[screen] = rememberScreenContext(screen, navigator, screenContextComponent)

      key(index) {
        BackHandler(enabled = handleBack && index > 0, onBack = action {
          navigator.pop(screen)
        })
      }
    }
  }

  // clean up removed contexts
  screenContexts.forEach { (screen, screenContext) ->
    key(screenContext) {
      DisposableEffect(true) {
        screenContext.scope.addObserver(
          object : ScopeObserver<Any> {
            override fun onExit(scope: Scope<Any>) {
              screenContexts.remove(screen)
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
    contentOpaque = { screenContexts[it]?.config?.opaque ?: false },
    transitionSpec = {
      val spec = (if (isPush)
        screenContexts[target]?.config?.enterTransitionSpec
      else
        screenContexts[initial]?.config?.exitTransitionSpec)
        ?: defaultTransitionSpec

      with(spec) { invoke() }
    }
  ) { screen ->
    screenContexts[screen]?.let {
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
