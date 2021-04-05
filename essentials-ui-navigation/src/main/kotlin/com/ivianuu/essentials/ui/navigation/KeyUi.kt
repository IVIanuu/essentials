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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Given
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.StateFlow

typealias KeyUi<K> = @Composable () -> Unit

typealias KeyUiFactory<K> = (K, KeyUiGivenScope) -> KeyUi<K>

class KeyUiModule<K : Key<*>>(private val keyClass: KClass<K>) {
    @Given
    fun keyUi(
        @Given keyUiFactory: (@Given K, @Given KeyUiGivenScope) -> KeyUi<K>
    ): Pair<KClass<Key<Any>>, KeyUiFactory<Key<Any>>> =
        (keyClass to keyUiFactory).cast()

    @Given
    fun keyUiOptionFactory(
        @Given keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
    ): Pair<KClass<Key<Any>>, KeyUiOptionsFactory<Key<Any>>> =
        (keyClass to keyUiOptionsFactory).cast()

    companion object {
        @Given
        inline operator fun <@Given T : KeyUi<K>, reified K : Key<*>> invoke() = KeyUiModule(K::class)
    }
}

typealias StateKeyUi<K, T, S> = @Composable (T, S) -> Unit

@Given
inline fun <@Given U : StateKeyUi<K, T, S>, reified K : Key<*>,
        T : StateFlow<S>, S> stateKeyUi(
    @Given noinline uiFactory: () ->U,
    @Given state: T
): KeyUi<K> = {
    val ui = remember(uiFactory) as @Composable (T, S) -> Unit
    ui(state, state.collectAsState().value)
}

typealias FeatureKeyUi<K, S, A> = @Composable (S, Collector<A>) -> Unit

@Given
inline fun <@Given U : FeatureKeyUi<K, S, A>, reified K : Key<*>, S, A> featureKeyUi(
    @Given noinline uiFactory: () -> U,
    @Given state: StateFlow<S>,
    @Given collector: Collector<A>
): KeyUi<K> = {
    val ui = remember(uiFactory) as @Composable (S, Collector<A>) -> Unit
    ui(state.collectAsState().value, collector)
}
