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
import com.ivianuu.injekt.Qualifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType
import kotlin.reflect.typeOf

typealias UiStateScope = CoroutineScope

@Given @Composable inline fun <reified S> uiStateFromFactory(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given noinline factory: (UiStateScope) -> StateFlow<S>
): StateFlow<S> = rememberRetained(key = typeOf<S>()) {
    UiStoreRunner(CoroutineScope(Job() + defaultDispatcher), factory)
}.store

@Given inline val <T> @Given StateFlow<T>.flow: Flow<T>
    get() = this

@Qualifier
@Target(AnnotationTarget.TYPE)
annotation class UiState

@Given @Composable inline val <T> @Given StateFlow<T>.latest: @UiState T
    get() = collectAsState().value

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
