/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.animation.AnimatedStackChild
import com.ivianuu.essentials.ui.backpress.BackHandler
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.collections.set
import kotlin.reflect.KClass

fun interface NavigationStateContent {
  @Composable operator fun invoke(p1: Modifier)
}

context(NamedCoroutineScope<AppScope>) @Provide fun navigationStateContent(
  navigator: Navigator,
  optionFactories: Map<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>,
  uiFactories: Map<KClass<Key<*>>, KeyUiFactory<Key<*>>>,
  decorateUi: (Scope<KeyUiScope>, Key<*>) -> DecorateKeyUi,
  elementsFactory: (Scope<KeyUiScope>, Key<*>) -> Elements<KeyUiScope>,
  rootKey: RootKey? = null
) = NavigationStateContent { modifier ->
  val backStack by navigator.backStack.collectAsState()

  if (backStack.size > 1)
    BackHandler {
      launch {
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
        var currentUi by remember { mutableStateOf<@Composable () -> Unit>({}) }
        val (keyUi, child) = remember {
          val scope = Scope<KeyUiScope>()
          val content = uiFactories[key::class]?.invoke(scope, key)
          checkNotNull(content) { "No ui factory found for $key" }
          val options = optionFactories[key::class]?.invoke(scope, key)
          content to NavigationContentStateChild(
            key = key,
            options = options,
            content = { currentUi },
            decorateKeyUi = decorateUi(scope, key),
            elements = elementsFactory(scope, key),
            scope = scope
          )
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

private class NavigationContentStateChild(
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
      decorateKeyUi.decorate {
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

