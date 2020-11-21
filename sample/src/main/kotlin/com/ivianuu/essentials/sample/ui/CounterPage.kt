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
import com.ivianuu.essentials.coroutines.collectIn
import com.ivianuu.essentials.sample.ui.CounterAction.Dec
import com.ivianuu.essentials.sample.ui.CounterAction.Inc
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.currentState
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CoroutineScope

@FunBinding
@Composable
fun CounterPage(
    dispatch: DispatchAction<CounterAction>,
    state: @UiState CounterState
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

@UiStateBinding
fun CounterStore(
    scope: CoroutineScope,
    initial: @Initial CounterState = CounterState(),
    actions: Actions<CounterAction>,
    showToast: showToast
) = scope.state(initial) {
    actions.collectIn(this) { action ->
        when (action) {
            Inc -> reduce { copy(count = count.inc()) }
            Dec -> if (currentState().count > 0) reduce { copy(count = count.dec()) }
            else showToast("Value cannot be less than 0!")
        }
    }
}

data class CounterState(val count: Int = 0)

sealed class CounterAction {
    object Inc : CounterAction()
    object Dec : CounterAction()
}
