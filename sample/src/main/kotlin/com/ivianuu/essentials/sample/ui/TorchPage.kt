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

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.launch

@FunBinding
@Composable
fun TorchPage(torchManager: TorchManager) {
    Scaffold(topBar = { TopAppBar(title = { Text("Torch") }) }) {
        val torchState = torchManager.torchState.collectAsState()

        Column(
            modifier = Modifier.center(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Torch is ${if (torchState.value) "enabled" else "disabled"}",
                style = MaterialTheme.typography.h4
            )
            Spacer(Modifier.preferredHeight(8.dp))
            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    scope.launch {
                        torchManager.toggleTorch()
                    }
                }
            ) {
                Text("Toggle torch")
            }
        }
    }
}
