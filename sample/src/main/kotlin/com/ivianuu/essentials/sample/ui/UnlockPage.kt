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

import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.screenstate.ScreenStateFlow
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.unlock.unlockScreen
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@FunBinding
@Composable
fun UnlockPage(
    screenStateFlow: ScreenStateFlow,
    showToast: showToast,
    unlockScreen: unlockScreen,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Unlock") }) }
    ) {
        val scope = rememberCoroutineScope()
        Button(
            modifier = Modifier.center(),
            onClick = {
                scope.launch {
                    showToast("Turn the screen off and on")

                    screenStateFlow
                        .filter { it == ScreenState.Locked }
                        .first()

                    val unlocked = unlockScreen()
                    showToast("Screen unlocked $unlocked")
                }
            }
        ) { Text("Unlock") }
    }
}
