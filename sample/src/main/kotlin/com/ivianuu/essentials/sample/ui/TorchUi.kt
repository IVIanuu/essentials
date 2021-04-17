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
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.torch.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Given
val torchHomeItem = HomeItem("Torch") { TorchKey }

object TorchKey : Key<Nothing>

@Given
fun torchUi(@Given torchState: MutableStateFlow<TorchState>): KeyUi<TorchKey> = {
    val torchEnabled by torchState.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Torch") }) }) {
        Column(
            modifier = Modifier.center(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Torch is ${if (torchEnabled) "enabled" else "disabled"}",
                style = MaterialTheme.typography.h4
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { torchState.update { !torchEnabled } }) {
                Text("Toggle torch")
            }
        }
    }
}
