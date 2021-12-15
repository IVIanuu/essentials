/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.animation.AnimatedStackChild
import com.ivianuu.essentials.ui.backpress.BackHandler
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.*
import kotlin.reflect.KClass

fun interface NavigationStateContent : @Composable (Modifier) -> Unit

@Provide fun navigationStateContent(
  navigator: Navigator,
  keyUiElementsFactory: (Scope<KeyUiScope>, Key<*>) -> Elements<KeyUiScope>,
  rootKey: RootKey? = null,
  S: NamedCoroutineScope<AppScope>
) = NavigationStateContent { modifier ->
  val backStack by navigator.backStack.collectAsState()

  val contentState = remember {
    NavigationContentState(keyUiElementsFactory, backStack)
  }

  DisposableEffect(backStack) {
    contentState.updateBackStack(backStack)
    onDispose {  }
  }

  if (backStack.size > 1)
    BackHandler {
      launch {
        navigator.popTop()
      }
    }

  LaunchedEffect(true) {
    onCancel {
      // it's important to clear the state
      // to prevent memory leaks
      contentState.updateBackStack(emptyList())

      if (rootKey != null)
        withTimeoutOrNull(100) {
          navigator.setRoot(rootKey)
        }
      else navigator.clear()
    }
  }

  AnimatedStack(modifier = modifier, children = contentState.stackChildren)
}

@Provide @Element<KeyUiScope>
data class NavigationContentComponent(
  val optionFactories: Map<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>,
  val uiFactories: Map<KClass<Key<*>>, KeyUiFactory<Key<*>>>,
  val decorateUi: DecorateKeyUi
)

private class NavigationContentState(
  var keyUiElementsFactory: (Scope<KeyUiScope>, Key<*>) -> Elements<KeyUiScope>,
  initialBackStack: List<Key<*>>
) {
  private var children by mutableStateOf(emptyList<Child>())

  val stackChildren: List<AnimatedStackChild<Key<*>>>
    get() = children.map { it.stackChild }

  init {
    updateBackStack(initialBackStack)
  }

  fun updateBackStack(backStack: List<Key<*>>) {
    val removedChildren = children
      .filter { it.key !in backStack }
    children = backStack
      .map { getOrCreateEntry(it) }
    removedChildren.forEach { it.detach() }
  }

  @Suppress("UNCHECKED_CAST")
  private fun getOrCreateEntry(key: Key<*>): Child {
    children.firstOrNull { it.key == key }?.let { return it }
    val scope = Scope<KeyUiScope>()
    val elements = keyUiElementsFactory(scope, key)
    val navigationContentComponent = elements<NavigationContentComponent>()
    val content = navigationContentComponent.uiFactories[key::class]?.invoke(key)
    checkNotNull(content) { "No ui factory found for $key" }
    val decoratedContent: @Composable () -> Unit = {
      navigationContentComponent.decorateUi {
        content()
      }
    }
    val options = navigationContentComponent.optionFactories[key::class]?.invoke(key)
    return Child(key, options, decoratedContent, elements, scope)
  }

  private class Child(
    val key: Key<*>,
    options: KeyUiOptions? = null,
    val content: @Composable () -> Unit,
    val elements: Elements<KeyUiScope>,
    val scope: Scope<KeyUiScope>
  ) {
    val stackChild = AnimatedStackChild(
      key = key,
      opaque = options?.opaque ?: false,
      enterTransition = options?.enterTransition,
      exitTransition = options?.exitTransition
    ) {
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
        content()

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
}
