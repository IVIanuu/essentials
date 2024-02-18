/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.animation.*
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
  val backStack = navigator.backStack.collect()

  val screenContexts = remember { mutableStateMapOf<Screen<*>, ScreenContext<*>>() }

  backStack.forEachIndexed { index, screen ->
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
          object : ScopeObserver<Any?> {
            override fun onExit(scope: Scope<Any?>) {
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
    items = backStack,
    contentOpaque = { screenContexts[it]?.config?.opaque ?: false },
    transitionSpec = {
      val spec = (if (isPush)
        screenContexts[target]?.config?.enterTransitionSpec
      else
        screenContexts[initial]?.config?.exitTransitionSpec)
        ?: defaultTransitionSpec

      spec()
    }
  ) {
    screenContexts[it]?.let { ScreenContent(it) }
  }
}

val LocalScreenTransitionSpec = compositionLocalOf<ElementTransitionSpec<Screen<*>>> { { } }
