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

import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide class KeycodeActionFactory(
  private val actionRootCommandRunner: ActionRootCommandRunner,
  private val resourceProvider: ResourceProvider,
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(ACTION_KEY_PREFIX)
  override suspend fun createAction(id: String): Action<*> {
    val keycode = id.removePrefix(ACTION_KEY_PREFIX)
    return Action<ActionId>(
      id = id,
      title = resourceProvider(R.string.es_action_keycode_suffix, keycode),
      icon = singleActionIcon(R.drawable.es_ic_keyboard),
      permissions = listOf(typeKeyOf<ActionRootPermission>()),
      unlockScreen = false,
      enabled = true
    )
  }

  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val keycode = id.removePrefix(ACTION_KEY_PREFIX)
    return { actionRootCommandRunner("input keyevent $keycode") }
  }
}

@Provide class KeycodeActionPickerDelegate(
  private val navigator: Navigator,
  private val resourceProvider: ResourceProvider,
) : ActionPickerDelegate {
  override val title: String
    get() = resourceProvider(R.string.es_action_keycode)
  override val icon: @Composable () -> Unit =
    { Icon(painterResource(R.drawable.es_ic_keyboard), null) }

  override suspend fun pickAction(): ActionPickerKey.Result? {
    val keycode = navigator.push(
      TextInputKey(
        title = resourceProvider<String>(R.string.es_keycode_picker_title),
        label = resourceProvider(R.string.es_keycode_input_hint),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        allowEmpty = false
      )
    )?.toIntOrNull() ?: return null

    return ActionPickerKey.Result.Action("$ACTION_KEY_PREFIX$keycode")
  }
}

private const val ACTION_KEY_PREFIX = "keycode=:="
