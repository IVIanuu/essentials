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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.toPublisher
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.base.EsViewModel
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Fail
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.asFail
import com.ivianuu.essentials.util.asSuccess
import com.ivianuu.essentials.util.ext.mainThread
import com.ivianuu.essentials.util.lifecycleOwner
import com.ivianuu.scopes.android.scope
import com.ivianuu.scopes.rx.disposeBy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * State view model
 */
abstract class MvRxViewModel<S>(initialState: S) : EsViewModel() {

    private val _liveData = MutableLiveData<S>(initialState)
    val liveData: LiveData<S> get() = _liveData

    private var _state: S = initialState
    val state: S get() = synchronized(this) { _state }

    protected fun setState(reducer: S.() -> S) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = synchronized(this@MvRxViewModel) { _state }
            val newState = reducer(currentState)
            if (currentState != newState) {
                synchronized(this@MvRxViewModel) { _state = newState }
                mainThread { _liveData.postValue(newState) }
            }
        }
    }

    fun logStateChanges() {
        subscribe { d { "new currentState -> $it" } }
    }

    protected fun subscribe(consumer: (S) -> Unit): Disposable {
        return Observable.fromPublisher(liveData.toPublisher(scope.lifecycleOwner))
            .subscribe(consumer)
            .disposeBy(scope)
    }

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
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        reducer: S.(Async<V>) -> S
    ): Job = viewModelScope.execute(
        context = context,
        start = start,
        block = { await() },
        reducer = reducer
    )

    protected fun <V> CoroutineScope.execute(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend () -> V,
        reducer: S.(Async<V>) -> S
    ): Job = launch(context, start) {
        setState { reducer(Loading()) }
        try {
            val result = block()
            setState { reducer(Success(result)) }
        } catch (e: Exception) {
            setState { reducer(Fail(e)) }
        }
    }

    override fun toString() = "${javaClass.simpleName} -> $state"
}