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
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.Scoped
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Effect
annotation class GlobalStateBinding {
    companion object {
        @Scoped(ApplicationComponent::class)
        @Binding
        fun <T : StateFlow<S>, S> globalStore(
            scope: GlobalScope,
            provider: (CoroutineScope) -> @ForEffect T,
        ): StateFlow<S> = provider(scope)

        @StateBinding
        inline fun <T : StateFlow<S>, S> StateFlow<S>.globalStateBindings(): StateFlow<S> = this
    }
}

@Effect
annotation class UiStateBinding {
    companion object {
        @Binding
        inline fun <reified T : StateFlow<S>, reified S> uiStateProducer(
            defaultDispatcher: DefaultDispatcher,
            noinline provider: (CoroutineScope) -> @ForEffect T
        ): UiStateProducer<S> = {
            uiStateImpl(defaultDispatcher, typeOf<T>(), provider)
        }

        @Binding
        @Composable
        fun <T : StateFlow<S>, S> uiStateProducer(
            producer: UiStateProducer<S>
        ): StateFlow<S> = producer()

        @StateBinding
        inline fun <T : StateFlow<S>, S> StateFlow<S>.uiStateBindings(): StateFlow<S> = this

        @PublishedApi
        @Composable
        internal fun <S> uiStateImpl(
            defaultDispatcher: DefaultDispatcher,
            type: KType,
            provider: (CoroutineScope) -> StateFlow<S>
        ): StateFlow<S> {
            return rememberRetained(key = type) {
                UiStoreRunner(CoroutineScope(Job() + defaultDispatcher), provider)
            }.store
        }
    }
}

// todo tmp workaround
typealias UiStateProducer<S> = @Composable () -> StateFlow<S>

@Effect
annotation class StateBinding {
    companion object {
        @Binding
        inline val <T : StateFlow<S>, S> @ForEffect T.flow: Flow<S>
            get() = this

        @Binding
        @Composable
        inline val <T : StateFlow<S>, S> @ForEffect T.latest: @UiState S
            get() = collectAsState().value
    }
}

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

@Qualifier
@Target(AnnotationTarget.TYPE)
annotation class UiState
