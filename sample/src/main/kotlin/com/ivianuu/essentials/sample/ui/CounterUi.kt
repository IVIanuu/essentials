/*
 * Copyright 2021 Manuel Wrage
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
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

@Provide val counterHomeItem = HomeItem("Counter") { CounterKey }

object CounterKey : Key<Unit>

@Provide val counterUi: ModelKeyUi<CounterKey, CounterModel> = {
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

@Optics data class CounterModel(
  val count: Int = 0,
  val inc: () -> Unit = {},
  val dec: () -> Unit = {}
)

@Provide fun counterModel(
  scope: InjektCoroutineScope<KeyUiScope>,
  toaster: Toaster
): @Scoped<KeyUiScope> StateFlow<CounterModel> = scope.state(CounterModel()) {
  action(CounterModel.inc()) { update { copy(count = count.inc()) } }
  action(CounterModel.dec()) {
    if (state.first().count > 0) update { copy(count = count.dec()) }
    else showToast("Value cannot be less than 0!")
  }
}
