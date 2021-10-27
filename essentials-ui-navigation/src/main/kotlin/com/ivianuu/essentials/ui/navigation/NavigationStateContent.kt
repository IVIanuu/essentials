/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.LocalComponent
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.animation.AnimatedStackChild
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.AppComponent
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.dispose
import com.ivianuu.injekt.common.entryPoint
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

typealias NavigationStateContent = @Composable (Modifier) -> Unit

@Provide fun navigationStateContent(
  appScope: ComponentScope<AppComponent>,
  navigator: Navigator,
  keyUiComponentFactory: KeyUiComponentFactory,
  rootKey: RootKey? = null
): NavigationStateContent = { modifier ->
  val backStack by navigator.backStack.collectAsState()

  val contentState = remember {
    NavigationContentState(keyUiComponentFactory, backStack)
  }

  DisposableEffect(backStack) {
    contentState.updateBackStack(backStack)
    onDispose {  }
  }

  BackHandler(enabled = backStack.size > 1) {
    appScope.launch {
      navigator.popTop()
    }
  }

  LaunchedEffect(true) {
    onCancel {
      if (rootKey != null) navigator.setRoot(rootKey)
      else navigator.clear()
    }
  }

  AnimatedStack(modifier = modifier, children = contentState.stackChildren)
}

@EntryPoint<KeyUiComponent> interface NavigationContentComponent {
  val optionFactories: Map<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>
  val uiFactories: Map<KClass<Key<*>>, KeyUiFactory<Key<*>>>
  val decorateUi: DecorateKeyUi
}

private class NavigationContentState(
  var keyUiComponentFactory: KeyUiComponentFactory,
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
    val keyUiComponent = keyUiComponentFactory.keyUiComponent(key)
    val navigationContentComponent = keyUiComponent.entryPoint<NavigationContentComponent>()
    val content = navigationContentComponent.uiFactories[key::class]?.invoke(key)
    checkNotNull(content) { "No ui factory found for $key" }
    val decoratedContent: @Composable () -> Unit = { navigationContentComponent.decorateUi(content) }
    val options = navigationContentComponent.optionFactories[key::class]?.invoke(key)
    return Child(key, options, decoratedContent, keyUiComponent)
  }

  private class Child(
    val key: Key<*>,
    options: KeyUiOptions? = null,
    val content: @Composable () -> Unit,
    val component: KeyUiComponent
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
        LocalComponent provides component,
        LocalKeyUiComponent provides component,
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

    private var savedState =
      mutableMapOf<Any, Map<String, List<Any?>>>()

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
      component.dispose()
    }
  }
}
