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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.gestures.action.executeAction
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.coroutines.UiScope
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.pushKeyForResult
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.launch

class ActionsKey

@KeyUiBinding<ActionsKey>
@FunBinding
@Composable
fun ActionsPage(
    executeAction: executeAction,
    pushKeyForResult: pushKeyForResult<ActionPickerKey, ActionPickerResult>,
    uiScope: UiScope,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Actions") }) }
    ) {
        Button(
            modifier = Modifier.center(),
            onClick = {
                uiScope.launch {
                    val action = pushKeyForResult(ActionPickerKey())
                        ?.let { it as? ActionPickerResult.Action }
                        ?.actionKey ?: return@launch

                    executeAction(action)
                }
            }
        ) { Text("Pick action") }
    }
}
