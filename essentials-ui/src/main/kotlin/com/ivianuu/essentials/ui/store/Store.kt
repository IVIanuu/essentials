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

interface StoreState
interface StoreAction

typealias StorePair<S, A> = Pair<S, (A) -> Unit>

@Composable
val <S, A> Store<S, A>.storePair: StorePair<S, A>
    get() = StorePair(snapshotState, remember(this) { { dispatch(it) } })

@Binding
@Composable
inline fun <reified S, reified A> storeFromProvider(
    defaultDispatcher: DefaultDispatcher,
    noinline provider: (CoroutineScope) -> Store<S, A>
): Store<S, A> {
    return rememberRetained(key = typeOf<Store<S, A>>()) {
        StoreRunner(CoroutineScope(Job() + defaultDispatcher), provider)
    }.store
}

@PublishedApi
internal class StoreRunner<S, A>(
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

@Composable
operator fun <S> Store<S, *>.component1() = snapshotState

@Composable
operator fun <A> Store<*, A>.component2(): (A) -> Unit = { dispatch(it) }

@FunBinding
@Composable
fun <S, A> rememberStore(
    rememberStoreViaFactory: rememberStoreViaFactory<S, A>,
    provider: (CoroutineScope) -> Store<S, A>
): Store<S, A> = rememberStoreViaFactory { provider(it) }

@FunBinding
@Composable
fun <S, A, P1> rememberStore1(
    rememberStoreViaFactory: rememberStoreViaFactory<S, A>,
    provider: (CoroutineScope, P1) -> Store<S, A>,
    @FunApi p1: P1
): Store<S, A> = key(p1) {
    rememberStoreViaFactory { provider(it, p1) }
}

@FunBinding
@Composable
fun <S, A, P1, P2> rememberStore2(
    rememberStoreViaFactory: rememberStoreViaFactory<S, A>,
    provider: (CoroutineScope, P1, P2) -> Store<S, A>,
    @FunApi p1: P1,
    @FunApi p2: P2
): Store<S, A> = key(p1, p2) {
    rememberStoreViaFactory { provider(it, p1, p2) }
}

@FunBinding
@Composable
fun <S, A, P1, P2, P3> rememberStore3(
    rememberStoreViaFactory: rememberStoreViaFactory<S, A>,
    provider: (CoroutineScope, P1, P2, P3) -> Store<S, A>,
    @FunApi p1: P1,
    @FunApi p2: P2,
    @FunApi p3: P3
): Store<S, A> = key(p1, p2, p3) {
    rememberStoreViaFactory { provider(it, p1, p2, p3) }
}

@FunBinding
@Composable
fun <S, A, P1, P2, P3, P4> rememberStore4(
    rememberStoreViaFactory: rememberStoreViaFactory<S, A>,
    provider: (CoroutineScope, P1, P2, P3, P4) -> Store<S, A>,
    @FunApi p1: P1,
    @FunApi p2: P2,
    @FunApi p3: P3,
    @FunApi p4: P4
): Store<S, A> = key(p1, p2, p3, p4) {
    rememberStoreViaFactory { provider(it, p1, p2, p3, p4) }
}

@FunBinding
@Composable
fun <S, A, P1, P2, P3, P4, P5> rememberStore5(
    rememberStoreViaFactory: rememberStoreViaFactory<S, A>,
    provider: (CoroutineScope, P1, P2, P3, P4, P5) -> Store<S, A>,
    @FunApi p1: P1,
    @FunApi p2: P2,
    @FunApi p3: P3,
    @FunApi p4: P4,
    @FunApi p5: P5
): Store<S, A> = key(p1, p2, p3, p4, p5) {
    rememberStoreViaFactory { provider(it, p1, p2, p3, p4, p5) }
}

@FunBinding
@Composable
fun <S, A> rememberStoreViaFactory(
    defaultDispatcher: DefaultDispatcher,
    @FunApi init: (CoroutineScope) -> Store<S, A>
): Store<S, A> {
    val scope = rememberRetainedCoroutinesScope { defaultDispatcher }
    return rememberRetained { init(scope) }
}
