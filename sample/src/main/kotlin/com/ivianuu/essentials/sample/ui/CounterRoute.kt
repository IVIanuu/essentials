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

package com.ivianuu.essentials.sample.ui

import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.composeControllerRoute
import com.ivianuu.essentials.ui.compose.material.EsTopAppBar
import com.ivianuu.essentials.ui.compose.material.Scaffold
import com.ivianuu.essentials.ui.compose.mvi.stateStore

val counterRoute = composeControllerRoute {
    Scaffold(
        appBar = { EsTopAppBar("Counter") },
        content = {
            Center {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val (state, dispatch) = +counterStateStore()

                    Text(
                        text = "Count: $state",
                        style = +themeTextStyle { h3 }
                    )

                    HeightSpacer(8.dp)

                    FloatingActionButton(
                        text = "Inc",
                        onClick = { dispatch(CounterAction.Inc) }
                    )

                    HeightSpacer(8.dp)

                    FloatingActionButton(
                        text = "dec",
                        onClick = { dispatch(CounterAction.Dec) }
                    )
                }
            }
        }
    )
}

private fun counterStateStore() = stateStore<Int, CounterAction>(
    initialState = { 1 },
    reducer = { currentState, action ->
        when (action) {
            CounterAction.Inc -> currentState + 1
            CounterAction.Dec -> currentState - 1
        }
    }
)

private enum class CounterAction { Inc, Dec }