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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.LocalUiGivenScope
import com.ivianuu.essentials.ui.animatedstack.AnimatedStack
import com.ivianuu.essentials.ui.animatedstack.AnimatedStackChild
import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.ChildGivenScopeFactory
import com.ivianuu.injekt.scope.element
import kotlin.reflect.KClass

typealias NavigationStateContent = @Composable (NavigationState, Modifier) -> Unit

@Given
fun navigationStateContent(
    @Given optionFactories: Map<KClass<Key<Any>>, KeyUiOptionsFactory<Key<Any>>>,
    @Given uiFactories: Map<KClass<Key<Any>>, KeyUiFactory<Key<Any>>>
): NavigationStateContent = { state, modifier ->
    val uiGivenScope = LocalUiGivenScope.current
    val contentState = remember {
        NavigationContentState(
            optionFactories = optionFactories,
            uiFactories = uiFactories,
            backStack = state.backStack.cast(),
            keyUiGivenScopeFactory = uiGivenScope.element<@ChildGivenScopeFactory () -> KeyUiGivenScope>()
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

private class NavigationContentState(
    var optionFactories: Map<KClass<Key<Any>>, KeyUiOptionsFactory<Key<Any>>> = emptyMap(),
    var uiFactories: Map<KClass<Key<Any>>, KeyUiFactory<Key<Any>>> = emptyMap(),
    var keyUiGivenScopeFactory: () -> KeyUiGivenScope,
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
        val component = keyUiGivenScopeFactory()
        val content = uiFactories[key::class]?.invoke(key, component)
        checkNotNull(content) { "No factory found for $key" }
        val options = optionFactories[key::class]?.invoke(key)
        return Child(key, options, content, component)
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
                LocalKeyUiGivenScope provides givenScope,
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
