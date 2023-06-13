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
    .mapIndexed { index, key ->
      key(key) {
        var currentModel by remember { mutableStateOf<Any?>(null) }
        val (keyUi, child) = remember {
          val scope = component.keyUiScopeFactory(navigator, key)
          val ui = component.uiFactories[key::class]?.invoke(navigator, scope, key)
          checkNotNull(ui) { "No ui factory found for $key" }
          val config = component.configFactories[key::class]?.invoke(navigator, scope, key)
          val model = component.modelFactories[key::class]?.invoke(navigator, scope, key)
          checkNotNull(model) { "No model found for $key" }
          model to NavigationContentStateChild(
            key = key,
            config = config,
            content = {
              with(ui as KeyUi<*, Any>) {
                with(currentModel as Any) {
                  invoke(this)
                }
              }
            },
            decorateKeyUi = component.decorateKeyUiFactory(navigator, scope, key),
            scope = scope
          )
        }

        key(index) {
          BackHandler(enabled = handleBack && (index > 0 || popRoot), onBackPress = action {
            navigator.pop(key)
          })
        }

        ObserveScope(
          remember {
            {
              currentModel = keyUi()

              DisposableEffect(true) {
                onDispose {
                  child.detach()
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
  key: Key<*>,
  config: KeyConfig<*>? = null,
  private val content: @Composable () -> Unit,
  private val decorateKeyUi: DecorateKeyUi,
  private val scope: Scope<KeyUiScope>
) {
  val stackChild = AnimatedStackChild(
    key = key,
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
      decorateKeyUi {
        content()
      }

      DisposableEffect(true) {
        isComposing = true
        onDispose {
          isComposing = false
          savedState[compositionKey] = savableStateRegistry.performSave()
          finalizeIfNeeded()
        }
      }
    }
  }

  private var savedState = mutableMapOf<Any, Map<String, List<Any?>>>()

  private var isComposing = false
  private var isDetached = false
  private var isFinalized = false

  fun detach() {
    isDetached = true
    finalizeIfNeeded()
  }

  private fun finalizeIfNeeded() {
    if (isFinalized) return
    if (isComposing || !isDetached) return
    isFinalized = true
    scope.dispose()
  }
}

@Provide @Service<UiScope> data class NavigationStateContentComponent(
  val configFactories: Map<KClass<Key<*>>, KeyConfigFactory<Key<*>>>,
  val uiFactories: Map<KClass<Key<*>>, KeyUiFactory<Key<*>>>,
  val modelFactories: Map<KClass<Key<*>>, ModelFactory<Key<*>, *>>,
  val decorateKeyUiFactory: (Navigator, Scope<KeyUiScope>, Key<*>) -> DecorateKeyUi,
  val keyUiScopeFactory: (Navigator, Key<*>) -> Scope<KeyUiScope>
)
