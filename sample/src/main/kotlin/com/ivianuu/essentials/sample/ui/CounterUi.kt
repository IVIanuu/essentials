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

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*

@Given
val counterHomeItem = HomeItem("Counter") { CounterKey }

object CounterKey : Key<Nothing>

@Given
val counterUi: ModelKeyUi<CounterKey, CounterModel> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Counter") }) }
    ) {
        Column(
            modifier = Modifier.center(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Count: ${model.count}",
                style = MaterialTheme.typography.h3
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("Inc") },
                onClick = model.inc
            )

            Spacer(Modifier.height(8.dp))

            ExtendedFloatingActionButton(
                text = { Text("dec") },
                onClick = model.dec
            )
        }
    }
}

@Optics
data class CounterModel(
    val count: Int = 0,
    val inc: () -> Unit = {},
    val dec: () -> Unit = {}
)

@Given
fun counterModel(
    @Given scope: GivenCoroutineScope<KeyUiGivenScope>,
    @Given toaster: Toaster
): @Scoped<KeyUiGivenScope> StateFlow<CounterModel> = scope.state(CounterModel()) {
    action(CounterModel.inc()) { update { copy(count = count.inc()) } }
    action(CounterModel.dec()) {
        if (state.first().count > 0) update { copy(count = count.dec()) }
        else toaster("Value cannot be less than 0!")
    }
}
