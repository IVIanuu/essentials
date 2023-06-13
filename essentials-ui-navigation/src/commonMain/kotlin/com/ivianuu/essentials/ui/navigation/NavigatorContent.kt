/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.animation.AnimatedStackChild
import com.ivianuu.essentials.ui.backpress.BackHandler
import com.ivianuu.injekt.Provide
import kotlin.collections.set
import kotlin.reflect.KClass

@Composable fun NavigatorContent(
  modifier: Modifier = Modifier,
  navigator: Navigator,
  handleBack: Boolean = true,
  popRoot: Boolean = false,
  componentFactory: @Composable () -> NavigationStateContentComponent = {
    val scope = LocalScope.current
    remember(scope) { scope.service() }
  }
) {
  val component = componentFactory()

  val backStack by navigator.backStack.collectAsState()

  val stackChildren = backStack
    .mapIndexed { index, screen ->
      key(screen) {
        var currentModel by remember { mutableStateOf<Any?>(null) }
        val (model, child) = remember {
          val scope = component.screenScopeFactory(navigator, screen)
          val ui = component.uiFactories[screen::class]?.invoke(navigator, scope, screen)
          checkNotNull(ui) { "No ui factory found for $screen" }
          val config = component.configFactories[screen::class]?.invoke(navigator, scope, screen)
          val model = component.modelFactories[screen::class]?.invoke(navigator, scope, screen)
          checkNotNull(model) { "No model found for $screen" }
          val decorateScreen = component.decorateScreenFactory(navigator, scope, screen)
          model to NavigationContentStateChild(
            screen = screen,
            config = config,
            content = {
              decorateScreen {
                with(ui as Ui<*, Any>) {
                  with(currentModel as Any) {
                    invoke(this)
                  }
                }
              }
            },
            scope = scope
          )
        }

        key(index) {
          BackHandler(enabled = handleBack && (index > 0 || popRoot), onBackPress = action {
            navigator.pop(screen)
          })
        }

        ObserveScope(
          remember {
            {
              currentModel = model()

              DisposableEffect(true) {
                onDispose {
                  child.remove()
                }
              }
            }
          }
        )

        child
      }
    }

  AnimatedStack(modifier = modifier, children = stackChildren.map { it.stackChild })
}

@Composable fun ObserveScope(body: @Composable () -> Unit) {
  body()
}

@Stable private class NavigationContentStateChild(
  screen: Screen<*>,
  config: ScreenConfig<*>? = null,
  private val scope: Scope<ScreenScope>,
  private val content: @Composable () -> Unit
) {
  val stackChild = AnimatedStackChild(
    key = screen,
    opaque = config?.opaque ?: false,
    enterTransition = config?.enterTransition,
    exitTransition = config?.exitTransition
  ) {
    if (isFinalized) return@AnimatedStackChild

    val compositionKey = currentCompositeKeyHash

    val savableStateRegistry = remember {
      SaveableStateRegistry(
        restoredValues = savedState.remove(compositionKey),
        canBeSaved = { true }
      )
    }

    CompositionLocalProvider(
      LocalScope provides scope,
      LocalSaveableStateRegistry provides savableStateRegistry
    ) {
      content()

      DisposableEffect(true) {
        isInComposition = true
        onDispose {
          isInComposition = false
          if (!isRemoved)
            savedState[compositionKey] = savableStateRegistry.performSave()
          finalizeIfNeeded()
        }
      }
    }
  }

  private var savedState = mutableMapOf<Any, Map<String, List<Any?>>>()

  private var isInComposition = false
  private var isRemoved = false
  private var isFinalized = false

  fun remove() {
    isRemoved = true
    finalizeIfNeeded()
  }

  private fun finalizeIfNeeded() {
    if (isFinalized) return
    if (isInComposition || !isRemoved) return
    isFinalized = true
    scope.dispose()
  }
}

@Provide @Service<UiScope> data class NavigationStateContentComponent(
  val configFactories: Map<KClass<Screen<*>>, ScreenConfigFactory<Screen<*>>>,
  val uiFactories: Map<KClass<Screen<*>>, UiFactory<Screen<*>>>,
  val modelFactories: Map<KClass<Screen<*>>, ModelFactory<Screen<*>, *>>,
  val decorateScreenFactory: (Navigator, Scope<ScreenScope>, Screen<*>) -> DecorateScreen,
  val screenScopeFactory: (Navigator, @Service<ScreenScope> Screen<*>) -> Scope<ScreenScope>
)
