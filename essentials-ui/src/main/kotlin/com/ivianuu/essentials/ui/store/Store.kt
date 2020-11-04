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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreScope
import com.ivianuu.essentials.store.setStateIn
import com.ivianuu.essentials.ui.common.rememberRetained
import com.ivianuu.essentials.ui.coroutines.rememberRetainedCoroutinesScope
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.flowAsResource
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.BindingAdapter
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.reflect.typeOf

fun <S, V> StoreScope<S, *>.execute(
    block: suspend () -> V,
    reducer: suspend S.(Resource<V>) -> S
): Job {
    return flow { emit(block()) }
        .executeIn(this, reducer)
}

fun <S, V> Flow<V>.executeIn(
    scope: StoreScope<S, *>,
    reducer: suspend S.(Resource<V>) -> S,
): Job = flowAsResource().setStateIn(scope, reducer)

typealias RetainedStore<S, A> = Store<S, A>

@BindingAdapter
annotation class StoreBinding {
    companion object {
        @Binding
        @Composable
        fun <T : Store<S, A>, S, A> RetainedStore<S, A>._storeState(): S = snapshotState

        @Binding
        @Composable
        fun <T : Store<S, A>, S, A> RetainedStore<S, A>._storeDispatch(): (A) -> Unit = remember(this) {
            { dispatch(it) }
        }

        @Binding
        @Composable
        inline fun <reified T : Store<S, A>, reified S, reified A> retainedStore(
            defaultDispatcher: DefaultDispatcher,
            noinline provider: (CoroutineScope) -> T
        ): RetainedStore<S, A> {
            return rememberRetained(key = typeOf<Store<S, A>>()) {
                RetainedStoreRunner(CoroutineScope(Job() + defaultDispatcher), provider)
            }.store
        }
    }
}

@PublishedApi
internal class RetainedStoreRunner<S, A>(
    private val coroutineScope: CoroutineScope,
    store: (CoroutineScope) -> Store<S, A>
) : DisposableHandle {
    val store = store(coroutineScope)
    override fun dispose() {
        coroutineScope.cancel()
    }
}

@Composable
val <S> Store<S, *>.snapshotState: S
    get() = state.collectAsState().value

