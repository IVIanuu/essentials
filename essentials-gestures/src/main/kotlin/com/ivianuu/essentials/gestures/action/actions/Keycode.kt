/*
 * Copyright 2021 Manuel Wrage
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
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ACTION_DELIMITER
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionRootPermission
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.dialog.TextInputKey
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide class KeycodeActionFactory(
  private val actionRootCommandRunner: ActionRootCommandRunner,
  private val RP: ResourceProvider
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val keycode = id.removePrefix(BASE_ID)
    return Action<ActionId>(
      id = id,
      title = loadResource(R.string.es_action_keycode_suffix, keycode),
      icon = staticActionIcon(R.drawable.es_ic_keyboard),
      permissions = listOf(typeKeyOf<ActionRootPermission>()),
      enabled = true
    )
  }

  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val keycode = id.removePrefix(BASE_ID)
    return ActionExecutor<ActionId> { actionRootCommandRunner("input keyevent $keycode") }
  }
}

@Provide class KeycodeActionPickerDelegate(
  private val navigator: Navigator,
  private val RP: ResourceProvider
) : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID
  override val title: String
    get() = loadResource(R.string.es_action_keycode)

  @Composable override fun Icon() {
    Icon(R.drawable.es_ic_keyboard)
  }

  override suspend fun pickAction(): ActionPickerKey.Result? {
    val keycode = navigator.push(
      TextInputKey(
        title = loadResource(R.string.es_keycode_picker_title),
        label = loadResource(R.string.es_keycode_input_hint),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        allowEmpty = false
      )
    )?.toIntOrNull() ?: return null

    return ActionPickerKey.Result.Action("$BASE_ID$keycode")
  }
}

private const val BASE_ID = "keycode$ACTION_DELIMITER"
