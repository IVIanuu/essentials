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

package com.ivianuu.essentials.ui.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.ui.common.rememberRetained
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForKey
import com.ivianuu.injekt.common.keyOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Qualifier annotation class UiStateBinding

@Macro
@Given
@Composable
fun <@ForKey T : @UiStateBinding StateFlow<S>, S> uiStateBindingImpl(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given provider: (@Given CoroutineScope) -> T
): StateFlow<S> = rememberRetained(keyOf<T>()) {
    UiStoreRunner(CoroutineScope(Job() + defaultDispatcher), provider)
}.store

@Given inline val <T> @Given StateFlow<T>.flow: Flow<T>
    get() = this

@PublishedApi
internal class UiStoreRunner<S>(
    private val coroutineScope: CoroutineScope,
    store: (CoroutineScope) -> StateFlow<S>
) : DisposableHandle {
    val store = store(coroutineScope)
    override fun dispose() {
        coroutineScope.cancel()
    }
}

@Qualifier annotation class UiState

@Given @Composable inline val <T> @Given StateFlow<T>.latest: @UiState T
    get() = collectAsState().value
