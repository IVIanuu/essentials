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

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.sample.ui.CounterAction.Dec
import com.ivianuu.essentials.sample.ui.CounterAction.Inc
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.common.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HomeItemBinding
@Given
val counterHomeItem = HomeItem("Counter") { CounterKey() }

class CounterKey : Key<Nothing>

@Module
val counterKeyModule = KeyModule<CounterKey>()

@Given
fun counterUi(
    @Given stateFlow: StateFlow<CounterState>,
    @Given dispatch: Collector<CounterAction>,
): KeyUi<CounterKey> = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Counter") }) }
    ) {
        Column(
            modifier = Modifier.center(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Count: ${state.count}",
                style = MaterialTheme.typography.h3
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("Inc") },
                onClick = { dispatch(Inc) }
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("dec") },
                onClick = { dispatch(Dec) }
            )
        }
    }
}

data class CounterState(val count: Int = 0)

sealed class CounterAction {
    object Inc : CounterAction()
    object Dec : CounterAction()
}

@Scoped<KeyUiComponent>
@Given
fun counterState(
    @Given scope: KeyUiScope,
    @Given initial: @Initial CounterState = CounterState(),
    @Given actions: Flow<CounterAction>,
    @Given toaster: Toaster
): StateFlow<CounterState> = scope.state(initial) {
    actions
        .filterIsInstance<Inc>()
        .reduce { copy(count = count.inc()) }
        .launchIn(this)

    actions
        .filterIsInstance<Dec>()
        .onEach {
            if (currentState().count > 0) reduce { copy(count = count.dec()) }
            else toaster.showToast("Value cannot be less than 0!")
        }
        .launchIn(this)
}

@Scoped<KeyUiComponent>
@Given
val counterActions get() = EventFlow<CounterAction>()
