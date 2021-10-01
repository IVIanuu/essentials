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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.LocalScope
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.animation.AnimatedStackChild
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.ChildScopeFactory
import com.ivianuu.injekt.scope.ScopeElement
import com.ivianuu.injekt.scope.requireElement
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

typealias NavigationStateContent = @Composable (Modifier) -> Unit

@Provide fun navigationStateContent(
  appScope: NamedCoroutineScope<AppScope>,
  navigator: Navigator,
  keyUiScopeFactory: @ChildScopeFactory (Key<*>) -> KeyUiScope
): NavigationStateContent = { modifier ->
  val backStack by navigator.backStack.collectAsState()

  val contentState = remember {
    NavigationContentState(keyUiScopeFactory, backStack)
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

  DisposableEffect(true) {
    onDispose {
      appScope.launch {
        navigator.backStack
          .first()
          .drop(1)
          .forEach { navigator.pop(it) }
      }
    }
  }

  AnimatedStack(modifier = modifier, children = contentState.stackChildren)
}

@Provide @ScopeElement<KeyUiScope>
class KeyUiComponent(
  val optionFactories: Map<KClass<Key<*>>, KeyUiOptionsFactory<Key<*>>>,
  val uiFactories: Map<KClass<Key<*>>, KeyUiFactory<Key<*>>>,
  val decorateUi: DecorateKeyUi,
)

private class NavigationContentState(
  var keyUiScopeFactory: (Key<*>) -> KeyUiScope,
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
    @Provide val scope = keyUiScopeFactory(key)
    val component = requireElement<KeyUiComponent>()
    val content = component.uiFactories[key::class]?.invoke(key, scope)
    checkNotNull(content) { "No ui factory found for $key" }
    val decoratedContent: @Composable () -> Unit = { component.decorateUi(content) }
    val options = component.optionFactories[key::class]?.invoke(key)
    return Child(key, options, decoratedContent, scope)
  }

  private class Child(
    val key: Key<*>,
    options: KeyUiOptions? = null,
    val content: @Composable () -> Unit,
    val scope: KeyUiScope
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
        LocalScope provides scope,
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
      scope.dispose()
    }
  }
}
