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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Given
import kotlin.reflect.KClass

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

typealias StoreKeyUi<K, S, A> = @Composable StoreKeyUiScope<K, S, A>.() -> Unit

@Composable
operator fun <S, A> StoreKeyUi<*, S, A>.invoke(
    state: S,
    sink: Sink<A>
) {
    invoke(
        object : StoreKeyUiScope<Nothing, S, A>, Sink<A> by sink {
            override val state: S
                get() = state
        }
    )
}

@Stable
interface StoreKeyUiScope<K, S, A> : Sink<A> {
    val state: S
}

@Given
fun <@Given U : StoreKeyUi<K, S, A>, K : Key<*>, S, A> storeKeyUi(
    @Given uiFactory: () -> U,
    @Given store: Store<S, A>
): KeyUi<K> = {
    val currentState by store.collectAsState()
    val scope = remember(store) {
        object : StoreKeyUiScope<K, S, A>, Sink<A> by store {
            override val state: S
                get() = currentState
        }
    }
    val ui = remember(uiFactory) as @Composable StoreKeyUiScope<K, S, A>.() -> Unit
    scope.ui()
}
