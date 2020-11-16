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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.sample.ui.CounterAction.Dec
import com.ivianuu.essentials.sample.ui.CounterAction.Inc
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.reduceState
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.store.Dispatch
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.State
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.essentials.ui.store.UiStoreBinding
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@FunBinding
@Composable
fun CounterPage(
    state: @UiState CounterState,
    dispatch: @Dispatch (CounterAction) -> Unit
) {
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

@UiStoreBinding
fun CoroutineScope.CounterStore(
    initial: @Initial CounterState = CounterState()
) = store<CounterState, CounterAction>(initial) {
    reduceState { action ->
        when (action) {
            Inc -> copy(count = count + 1)
            Dec -> copy(count = count - 1)
        }
    }
}

data class CounterState(val count: Int = 0)

sealed class CounterAction {
    object Inc : CounterAction()
    object Dec : CounterAction()
}
