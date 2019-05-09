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
import androidx.lifecycle.viewModelScope
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.ui.mvrx.lifecycle.LifecycleStateListener
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.asFail
import com.ivianuu.essentials.util.asSuccess
import com.ivianuu.scopes.android.scope
import com.ivianuu.scopes.rx.disposeBy
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

    private val stateStore = MvRxStateStore(initialState)
        .also { it.disposeBy(scope) }

    fun peekState(): S = stateStore.state

    protected fun withState(consumer: (S) -> Unit) {
        stateStore.get(consumer)
    }

    protected fun setState(reducer: S.() -> S) {
        stateStore.set(reducer)
    }

    fun logStateChanges() {
        subscribe { d { "new state -> $it" } }
    }

    protected fun subscribe(consumer: (S) -> Unit): Disposable =
        stateStore.observable.subscribe(consumer).disposeBy(scope)

    fun subscribe(owner: LifecycleOwner, consumer: (S) -> Unit): Disposable {
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
        return viewModelScope.launch {
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