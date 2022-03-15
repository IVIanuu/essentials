/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.backpress.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlin.collections.set
import kotlin.reflect.*

fun interface NavigationStateContent {
  @Composable operator fun invoke(p1: Modifier)
}

@Provide fun navigationStateContent(
  navigator: Navigator,
  keyUiElementsFactory: (Scope<KeyUiScope>, Key<*>) -> Elements<KeyUiScope>,
  rootKey: RootKey? = null,
  scope: NamedCoroutineScope<AppScope>
) = NavigationStateContent { modifier ->
  val backStack by navigator.backStack.collectAsState()

  if (backStack.size > 1)
    BackHandler {
      scope.launch {
        navigator.popTop()
      }
    }

  LaunchedEffect(true) {
    onCancel {
      if (rootKey != null)
        withTimeoutOrNull(100) {
          navigator.setRoot(rootKey)
        }
      else navigator.clear()
    }
  }

  val stackChildren = backStack
    .map { key ->
      key(key) {
        val currentUi = remember { mutableStateOf<@Composable () -> Unit>({}) }
        val (keyUi, child) = remember {
          val scope = Scope<KeyUiScope>()
          val elements = keyUiElementsFactory(scope, key)
          val navigationContentComponent = elements<NavigationContentComponent>()
          val content = navigationContentComponent.uiFactories[key::class]?.invoke(key)
          checkNotNull(content) { "No ui factory found for $key" }
          val options = navigationContentComponent.optionFactories[key::class]?.invoke(key)
          content to NavigationContentStateChild(
            key = key,
            options = options,
            content = { currentUi.value },
            decorateKeyUi = navigationContentComponent.decorateUi,
            elements = elements,
            scope = scope
          )
        }

        ObserveScope(
          remember {
            {
              currentUi.value = keyUi()

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

private class NavigationContentStateChild(
  private val key: Key<*>,
  options: KeyUiOptions? = null,
  content: () -> @Composable () -> Unit,
  private val decorateKeyUi: DecorateKeyUi,
  private val elements: Elements<KeyUiScope>,
  private val scope: Scope<KeyUiScope>
) {
  var content by mutableStateOf(content)

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
      decorateKeyUi(content())

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

@Provide @Element<KeyUiScope>
data class NavigationContentComponent(
  val optionFactories: Map<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>,
  val uiFactories: Map<KClass<Key<*>>, KeyUiFactory<Key<*>>>,
  val decorateUi: DecorateKeyUi
)
