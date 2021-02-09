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

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionFactoryBinding
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionPickerDelegateBinding
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.ui.dialog.TextInputKey
import com.ivianuu.essentials.ui.navigation.pushKeyForResult
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.essentials.util.stringResourceWithArguments
import com.ivianuu.injekt.Given

@ActionFactoryBinding
@Given
class KeycodeActionFactory(
    @Given private val permissions: ActionPermissions,
    @Given private val runRootCommand: runRootCommand,
    @Given private val stringResourceWithArguments: stringResourceWithArguments,
) : ActionFactory {
    override suspend fun handles(id: String): Boolean = id.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(id: String): Action {
        val keycode = id.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            id = id,
            title = stringResourceWithArguments(R.string.es_action_keycode_suffix, listOf(keycode)),
            icon = singleActionIcon(R.drawable.es_ic_keyboard),
            permissions = listOf(permissions.root),
            unlockScreen = false,
            enabled = true
        )
    }

    override suspend fun createExecutor(id: String): ActionExecutor {
        val keycode = id.removePrefix(ACTION_KEY_PREFIX)
        return { runRootCommand("input keyevent $keycode") }
    }
}

@ActionPickerDelegateBinding
@Given
class KeycodeActionPickerDelegate(
    @Given private val pickActionKeycode: pushKeyForResult<TextInputKey, String>,
    @Given private val stringResource: stringResource,
) : ActionPickerDelegate {
    override val title: String
        get() = stringResource(R.string.es_action_keycode)
    override val icon: @Composable () -> Unit
        get() = { Icon(R.drawable.es_ic_keyboard) }

    override suspend fun getResult(): ActionPickerResult? {
        val keycode = pickActionKeycode(
            TextInputKey(
                title = stringResource(R.string.es_keycode_picker_title),
                label = stringResource(R.string.es_keycode_input_hint),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                allowEmpty = false
            )
        )?.toIntOrNull() ?: return null

        return ActionPickerResult.Action("$ACTION_KEY_PREFIX$keycode")
    }
}

private const val ACTION_KEY_PREFIX = "keycode=:="
