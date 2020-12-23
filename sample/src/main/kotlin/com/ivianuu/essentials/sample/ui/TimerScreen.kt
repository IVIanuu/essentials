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

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@HomeItemBinding("Timer")
class TimerKey

@KeyUiBinding<TimerKey>
@GivenFun
@Composable
fun TimerScreen(state: @UiState TimerState) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Timer") }) }
    ) {
        Text(
            text = "Value: ${state.value}",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.center()
        )
    }
}

data class TimerState(val value: Int = 0)

@UiStateBinding
fun timerState(
    scope: CoroutineScope,
    initial: @Initial TimerState = TimerState()
) = scope.state(initial) {
    while (coroutineContext.isActive) {
        reduce { copy(value = value.inc()) }
        delay(1000)
    }
}
