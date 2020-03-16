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

import androidx.ui.layout.Center
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.common.SimpleScreen
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.currentTextComposableStyle
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.Route

val TorchRoute = Route {
    SimpleScreen(title = "Torch") {
        Center {
            val torchManager = inject<TorchManager>()
            val torchState = collect(torchManager.torchState, false)

            Column(crossAxisAlignment = CrossAxisAlignment.Center) {
                Text(
                    "Torch is ${if (torchState) "enabled" else "disabled"}",
                    style = currentTextComposableStyle()
                        .copy(textStyle = MaterialTheme.typography().h4)
                )
                Spacer(LayoutHeight(8.dp))
                Button(
                    onClick = launchOnClick { torchManager.toggleTorch() }
                ) {
                    Text("Toggle torch")
                }
            }
        }
    }
}