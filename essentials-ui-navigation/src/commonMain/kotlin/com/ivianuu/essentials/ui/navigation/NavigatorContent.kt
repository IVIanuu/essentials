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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.LocalUiElements
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.animation.AnimatedStackChild
import com.ivianuu.essentials.ui.backpress.BackHandler
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import kotlin.collections.set
import kotlin.reflect.KClass

@Composable fun NavigatorContent(
  modifier: Modifier = Modifier,
  navigator: Navigator,
  handleBack: Boolean = true,
  popRoot: Boolean = false,
  componentFactory: @Composable () -> NavigationStateContentComponent = {
    LocalUiElements.current.element()
  }
) {
  val component = componentFactory()

  val backStack by navigator.backStack.collectAsState()

  val stackChildren = backStack
    .mapIndexed { index, key ->
      key(key) {
        var currentUi by remember { mutableStateOf<@Composable () -> Unit>({}) }
        val (keyUi, child) = remember {
          val scope = Scope<KeyUiScope>()
          val content = component.uiFactories[key::class]?.invoke(navigator, scope, key)
          checkNotNull(content) { "No ui factory found for $key" }
          val options = component.optionFactories[key::class]?.invoke(navigator, scope, key)
          content to NavigationContentStateChild(
            key = key,
            options = options,
            content = { currentUi },
            decorateKeyUi = component.decorateKeyUi(navigator, scope, key),
            elements = component.elementsFactory(navigator, scope, key),
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
              currentUi = keyUi()

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
  options: KeyUiOptions? = null,
  private val content: () -> @Composable () -> Unit,
  private val decorateKeyUi: DecorateKeyUi,
  private val elements: Elements<KeyUiScope>,
  private val scope: Scope<KeyUiScope>
) {
  val stackChild = AnimatedStackChild(
    key = key,
    opaque = options?.opaque ?: false,
    enterTransition = options?.enterTransition,
    exitTransition = options?.exitTransition
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
      LocalKeyUiElements provides elements,
      LocalSaveableStateRegistry provides savableStateRegistry
    ) {
      decorateKeyUi {
        content()()
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

@Provide @Element<UiScope> data class NavigationStateContentComponent(
  val optionFactories: Map<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>,
  val uiFactories: Map<KClass<Key<*>>, KeyUiFactory<Key<*>>>,
  val decorateKeyUi: (Navigator, Scope<KeyUiScope>, Key<*>) -> DecorateKeyUi,
  val elementsFactory: (Navigator, Scope<KeyUiScope>, Key<*>) -> Elements<KeyUiScope>
)
