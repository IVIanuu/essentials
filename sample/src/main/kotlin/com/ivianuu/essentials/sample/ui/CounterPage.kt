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

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.material.ExtendedFloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.onEachAction
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.ui.store.setState
import com.ivianuu.essentials.ui.store.store
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.CoroutineScope

@Reader
@Composable
fun CounterPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Counter") }) }
    ) {
        Column(
            modifier = Modifier.center(),
            verticalArrangement = Arrangement.Center,
            horizontalGravity = Alignment.CenterHorizontally
        ) {
            val (state, dispatch) = rememberStore { counterStore() }

            Text(
                text = "Count: ${state.count}",
                style = MaterialTheme.typography.h3
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("Inc") },
                onClick = { dispatch(CounterAction.Inc) }
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("dec") },
                onClick = { dispatch(CounterAction.Dec) }
            )
        }
    }
}

@Reader
private fun CoroutineScope.counterStore() =
    store<CounterState, CounterAction>(CounterState(0)) {
        onEachAction {
            when (it) {
                CounterAction.Inc -> setState { copy(count = count + 1) }
                CounterAction.Dec -> setState { copy(count = count - 1) }
            }.exhaustive
        }
    }

private data class CounterState(val count: Int)
private enum class CounterAction { Inc, Dec }
