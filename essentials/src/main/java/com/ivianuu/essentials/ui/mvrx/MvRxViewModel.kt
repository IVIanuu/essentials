/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.LifecycleOwner
import com.ivianuu.essentials.ui.common.EsViewModel
import com.ivianuu.essentials.ui.mvrx.lifecycle.LifecycleStateListener
import com.ivianuu.essentials.util.*
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.statestore.Consumer
import com.ivianuu.statestore.Reducer
import com.ivianuu.statestore.StateStore
import com.ivianuu.statestore.rx.asObservable
import com.ivianuu.timberktx.d
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * State view model
 */
abstract class MvRxViewModel<S>(initialState: S) : EsViewModel() {

    private val stateStore = StateStore(initialState)

    fun peekState(): S = stateStore.peekState()

    protected fun withState(consumer: Consumer<S>) {
        stateStore.withState(consumer)
    }

    protected fun setState(reducer: Reducer<S>) {
        stateStore.setState(reducer)
    }

    fun logStateChanges() {
        subscribe { d { "new state -> $it" } }
    }

    protected fun subscribe(consumer: Consumer<S>): Disposable =
        stateStore.asObservable().subscribe(consumer).disposeBy(scope)

    fun subscribe(owner: LifecycleOwner, consumer: Consumer<S>): Disposable {
        return LifecycleStateListener(
            owner,
            stateStore,
            consumer
        )
    }

    protected fun Completable.execute(
        reducer: S.(Async<Unit>) -> S
    ): Disposable = toSingle { Unit }.execute(reducer)

    protected fun <V> Single<V>.execute(
        reducer: S.(Async<V>) -> S
    ): Disposable = toObservable().execute(reducer)

    protected fun <V> Observable<V>.execute(
        reducer: S.(Async<V>) -> S
    ): Disposable {
        setState { reducer(Loading()) }

        return this
            .map { it.asSuccess() as Async<V> }
            .onErrorReturn { it.asFail() }
            .subscribe { setState { reducer(it) } }
    }

    protected fun <V> Deferred<V>.execute(
        reducer: S.(Async<V>) -> S
    ): Job {
        setState { reducer(Loading()) }
        return coroutineScope.launch {
            val result = try {
                await().asSuccess()
            } catch (e: Exception) {
                e.asFail<V>()
            }

            setState { reducer(result) }
        }
    }

    override fun toString() = "${javaClass.simpleName} -> ${peekState()}"
}