/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.compose.*
import com.ivianuu.injekt.scope.*
import kotlin.reflect.*

typealias NavigationStateContent = @Composable (NavigationState, Modifier) -> Unit

@Given
val navigationStateContent: NavigationStateContent = { state, modifier ->
    val keyUiGivenScopeFactory = element<@ChildScopeFactory (Key<*>) -> KeyUiGivenScope>()
    val contentState = remember {
        NavigationContentState(
            backStack = state.backStack.cast(),
            keyUiGivenScopeFactory = keyUiGivenScopeFactory
        )
    }
    SideEffect {
        contentState.updateBackStack(state.backStack.cast())
    }
    DisposableEffect(true) {
        onDispose {
            contentState.updateBackStack(emptyList())
        }
    }
    AnimatedStack(modifier = modifier, children = contentState.stackChildren)
}

@Given
@InstallElement<KeyUiGivenScope>
class KeyUiComponent(
    @Given val optionFactories: Map<KClass<Key<Any>>, KeyUiOptionsFactory<Key<Any>>>,
    @Given val uiFactories: Map<KClass<Key<Any>>, KeyUiFactory<Key<Any>>>,
    @Given val decorateUi: DecorateKeyUi,
)

private class NavigationContentState(
    var keyUiGivenScopeFactory: (Key<*>) -> KeyUiGivenScope,
    backStack: List<Key<Any>>,
) {
    private var children by mutableStateOf(emptyList<Child>())

    val stackChildren: List<AnimatedStackChild<Key<Any>>>
        get() = children.map { it.stackChild }

    init {
        updateBackStack(backStack)
    }

    fun updateBackStack(backStack: List<Key<Any>>) {
        val removedChildren = children
            .filter { it.key !in backStack }
        children = backStack
            .map { getOrCreateEntry(it) }
        removedChildren.forEach { it.detach() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getOrCreateEntry(key: Key<Any>): Child {
        children.firstOrNull { it.key == key }?.let { return it }
        val scope = keyUiGivenScopeFactory(key)
        val component = scope.element<KeyUiComponent>()
        val content = component.uiFactories[key::class]?.invoke(key, scope)
        checkNotNull(content) { "No factory found for $key" }
        val decoratedContent: @Composable () -> Unit = { component.decorateUi(content) }
        val options = component.optionFactories[key::class]?.invoke(key)
        return Child(key, options, decoratedContent, scope)
    }

    private class Child(
        val key: Key<Any>,
        options: KeyUiOptions? = null,
        val content: @Composable () -> Unit,
        val givenScope: KeyUiGivenScope
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
                LocalGivenScope provides givenScope,
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
            givenScope.dispose()
        }
    }
}
