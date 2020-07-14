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
import com.ivianuu.essentials.ui.coroutines.compositionScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.unlock.UnlockScreen
import com.ivianuu.essentials.unlock.unlockScreen
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Reader
@Composable
fun UnlockPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Unlock") }) }
    ) {
        val scope = compositionScope()
        Button(
            modifier = Modifier.center(),
            onClick = {
                scope.launch {
                    Toaster.toast("Turn the screen off and on")

                    given<ScreenStateProvider>().screenState
                        .filter { it == ScreenState.Locked }
                        .first()

                    val unlocked = unlockScreen()
                    Toaster.toast("Screen unlocked $unlocked")
                }
            }
        ) { Text("Unlock") }
    }
}
