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
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

typealias KeyUi<K> = @Composable () -> Unit

typealias KeyUiFactory<K> = (K, KeyUiGivenScope) -> KeyUi<K>

@Given
class KeyUiModule<@Given T : KeyUi<K>, K : Key<*>> {
    @Given
    fun keyUi(
        @Given keyClass: KClass<K>,
        @Given keyUiFactory: (@Given K, @Given KeyUiGivenScope) -> KeyUi<K>
    ): Pair<KClass<Key<Any>>, KeyUiFactory<Key<Any>>> =
        (keyClass to keyUiFactory).cast()

    @Given
    fun keyUiOptionFactory(
        @Given keyClass: KClass<K>,
        @Given keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
    ): Pair<KClass<Key<Any>>, KeyUiOptionsFactory<Key<Any>>> =
        (keyClass to keyUiOptionsFactory).cast()
}

typealias ModelKeyUi<K, S> = @Composable ModelKeyUiScope<K, S>.() -> Unit

@Composable
operator fun <S> ModelKeyUi<*, S>.invoke(model: S) {
    invoke(
        object : ModelKeyUiScope<Nothing, S> {
            override val model: S
                get() = model
        }
    )
}

@Stable
interface ModelKeyUiScope<K, S> {
    val model: S
}

@Given
fun <@Given U : ModelKeyUi<K, S>, K : Key<*>, S> modelKeyUi(
    @Given uiFactory: () -> U,
    @Given model: StateFlow<S>
): KeyUi<K> = {
    val currentModel by model.collectAsState()
    val scope = remember {
        object : ModelKeyUiScope<K, S> {
            override val model: S
                get() = currentModel
        }
    }
    val ui = remember(uiFactory) as @Composable ModelKeyUiScope<K, S>.() -> Unit
    scope.ui()
}
