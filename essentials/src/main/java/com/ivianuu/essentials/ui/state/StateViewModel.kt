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

package com.ivianuu.essentials.ui.state

import com.ivianuu.essentials.ui.common.BaseViewModel
import com.ivianuu.essentials.util.ext.behaviorSubject
import io.reactivex.Observable

/**
 * State view model
 */
abstract class StateViewModel<T : Any> : BaseViewModel() {

    val state: Observable<T>
        get() = _state
    private val _state = behaviorSubject<T>()

    protected inline fun editState(transformer: (T) -> T) {
        withState { updateState(transformer.invoke(it)) }
    }

    protected inline fun withState(action: (T) -> Unit) {
        action.invoke(requireState())
    }

    protected fun updateState(newState: T) {
        _state.onNext(newState)
    }

    protected fun requireState() = peekState() ?: throw IllegalStateException("state is null")

    protected fun peekState() = _state.value
}