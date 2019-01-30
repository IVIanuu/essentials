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

package com.ivianuu.essentials.sample.ui.counter

import android.content.Context
import com.ivianuu.essentials.injection.CONTROLLER_SCOPE
import com.ivianuu.essentials.sample.ui.list.ListKey
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.horizontal
import com.ivianuu.essentials.util.ext.toast
import com.ivianuu.injekt.annotations.Factory
import com.ivianuu.timberktx.d
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.popToRoot

/**
 * Counter view model
 */
@Factory(scopeName = CONTROLLER_SCOPE)
class CounterViewModel(
    key: CounterKey,
    private val context: Context,
    private val router: Router
) : MvRxViewModel<CounterState>(CounterState(key.screen)) {

    override fun onCleared() {
        super.onCleared()
        d { "on cleared" }
    }

    fun increaseClicked() {
        setState { copy(count = count.inc()) }
    }

    fun decreaseClicked() {
        setState {
            if (count > 0) {
                copy(count = count.dec())
            } else {
                copy(count = 0)
            }
        }
    }

    fun resetClicked() {
        setState { copy(count = 0) }
    }

    fun screenUpClicked() {
        d { "screen up clicked" }
        withState {
            d { "navigate" }
            router.navigate(
                CounterKey(it.screen.inc()),
                NavOptions().horizontal()
            )
        }
    }

    fun screenDownClicked() {
        router.goBack()
    }

    fun rootScreenClicked() {
        router.popToRoot()
    }

    fun listScreenClicked() {
        router.navigate(ListKey())
    }

    fun doWorkClicked() {
        context.toast("Not implemented")
    }
}

data class CounterState(
    val screen: Int,
    val count: Int = 0
)