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
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.gestures.action.executeAction
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.coroutines.UiScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.pushForResult
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import kotlinx.coroutines.launch

@HomeItemBinding
@Given
val actionsHomeItem: HomeItem = HomeItem("Actions") { ActionsKey() }

class ActionsKey : Key<Nothing>

@Module
val actionsKeyModule = KeyModule<ActionsKey>()

@Given
fun actionsUi(
    @Given executeAction: executeAction,
    @Given navigator: Collector<NavigationAction>,
    @Given uiScope: UiScope,
): KeyUi<ActionsKey> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Actions") }) }
    ) {
        Button(
            modifier = Modifier.center(),
            onClick = {
                uiScope.launch {
                    val action = navigator.pushForResult(ActionPickerKey())
                        ?.let { it as? ActionPickerKey.Result.Action }
                        ?.actionKey ?: return@launch

                    executeAction(action)
                }
            }
        ) { Text("Pick action") }
    }
}
