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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.sample.ui.CounterAction.*
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.State
import com.ivianuu.essentials.store.actionsOf
import com.ivianuu.essentials.store.collectIn
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.first

@Given
val counterHomeItem = HomeItem("Counter") { CounterKey() }

class CounterKey : Key<Nothing>

@Given
val counterUi: StoreKeyUi<CounterKey, CounterState, CounterAction> = {
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
                onClick = { tryEmit(Inc) }
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("dec") },
                onClick = { tryEmit(Dec) }
            )
        }
    }
}

data class CounterState(val count: Int = 0) : State()

sealed class CounterAction {
    object Inc : CounterAction()
    object Dec : CounterAction()
}

@Given
fun counterStore(
    @Given toaster: Toaster
): StoreBuilder<KeyUiGivenScope, CounterState, CounterAction> = {
    actionsOf<Inc>()
        .updateIn(this) { copy(count = count.inc()) }
    actionsOf<Dec>()
        .collectIn(this) {
            if (state.first().count > 0) update { copy(count = count.dec()) }
            else toaster.showToast("Value cannot be less than 0!")
        }
}
