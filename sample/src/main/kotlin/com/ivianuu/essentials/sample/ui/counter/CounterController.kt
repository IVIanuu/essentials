/*
 * Copyright 2019 Manuel Wrage
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

/**
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.test.ControllerContext
import com.ivianuu.essentials.sample.ui.test.MviAction
import com.ivianuu.essentials.sample.ui.test.MviState
import com.ivianuu.essentials.sample.ui.test.Reducer
import com.ivianuu.essentials.sample.ui.test.StateStore
import com.ivianuu.essentials.sample.ui.test.mvRxControllerRoute
import com.ivianuu.essentials.sample.ui.test.stateStore
import kotlinx.android.synthetic.main.controller_counter.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

fun ComponentComposition.counterRoute() = mvRxControllerRoute(R.layout.controller_counter) {
val (state, dispatch) = stateStore(initialState = CountState(0), reducer = countReducer)
screen_up.setOnClickListener { dispatch(CountAction.Inc) }
screen_down.setOnClickListener { dispatch(CountAction.Dec) }
count_text.text = "Count: ${state.count}"
}

private data class CountState(val count: Int) : MviState

private enum class CountAction : MviAction { Inc, Dec }

private val countReducer: Reducer<CountState, CountAction> = { action ->
when(action) {
CountAction.Inc -> copy(count = count + 1)
CountAction.Dec -> copy(count = count - 1)
    }
}*/