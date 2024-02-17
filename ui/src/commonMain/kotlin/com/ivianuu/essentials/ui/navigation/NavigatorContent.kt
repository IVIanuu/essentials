/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.LocalScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.ScopeObserver
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.animation.ElementTransitionSpec
import com.slack.circuit.foundation.internal.BackHandler
import kotlin.collections.set

@Composable fun NavigatorContent(
  modifier: Modifier = Modifier,
  navigator: Navigator,
  handleBack: Boolean = true,
  defaultTransitionSpec: ElementTransitionSpec<Screen<*>> = LocalScreenTransitionSpec.current
) {
  NavigatorContent<RootNavGraph>(
    modifier,
    navigator,
    LocalScope.current.service(),
    handleBack,
    defaultTransitionSpec
  )
}

@Composable fun <N> NavigatorContent(
  modifier: Modifier = Modifier,
  navigator: Navigator,
  screenContextComponent: ScreenContextComponent<N>,
  handleBack: Boolean = true,
  defaultTransitionSpec: ElementTransitionSpec<Screen<*>> = LocalScreenTransitionSpec.current
) {
  val backStack by navigator.backStack.collectAsState()

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
