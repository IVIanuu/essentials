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

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.screenstate.ScreenState
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HomeItemBinding
@Given
val unlockHomeItem = HomeItem("Unlock") { UnlockKey() }

class UnlockKey : Key<Nothing>

@Module
val unlockKeyModule = KeyModule<UnlockKey>()

@Given
fun unlockUi(
    @Given screenState: Flow<ScreenState>,
    @Given screenUnlocker: ScreenUnlocker,
    @Given toaster: Toaster,
): KeyUi<UnlockKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Unlock") }) }
    ) {
        val scope = rememberCoroutineScope()
        Button(
            modifier = Modifier.center(),
            onClick = {
                scope.launch {
                    toaster.showToast("Turn the screen off and on")
                    screenState.first { it == ScreenState.Locked }
                    val unlocked = screenUnlocker()
                    toaster.showToast("Screen unlocked $unlocked")
                }
            }
        ) { Text("Unlock") }
    }
}
