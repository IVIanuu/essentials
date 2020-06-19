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
import androidx.compose.collectAsState
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Transient

@Transient
class TorchPage(
    private val torchManager: TorchManager
) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Torch") }) }
        ) {
            val torchState = torchManager.torchState.collectAsState()

            Column(
                modifier = Modifier.center(),
                horizontalGravity = Alignment.CenterHorizontally
            ) {
                Text(
                    "Torch is ${if (torchState.value) "enabled" else "disabled"}",
                    style = MaterialTheme.typography.h4
                )
                Spacer(Modifier.preferredHeight(8.dp))
                Button(
                    onClick = launchOnClick { torchManager.toggleTorch() }
                ) {
                    Text("Toggle torch")
                }
            }
        }
    }
}
