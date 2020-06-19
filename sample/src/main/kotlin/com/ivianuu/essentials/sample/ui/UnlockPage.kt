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
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.screenstate.ScreenStateProvider
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.unlock.UnlockScreen
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Transient
class UnlockPage(
    private val screenStateProvider: ScreenStateProvider,
    private val unlockScreen: UnlockScreen,
    private val toaster: Toaster
) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Unlock") }) }
        ) {
            Button(
                modifier = Modifier.center(),
                onClick = launchOnClick {
                    toaster.toast("Turn the screen off and on")

                    screenStateProvider.screenState
                        .filter { it == ScreenState.Locked }
                        .first()

                    val unlocked = unlockScreen()
                    toaster.toast("Screen unlocked $unlocked")
                }
            ) { Text("Unlock") }
        }
    }
}
