/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ACTION_DELIMITER
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionRootPermission
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerScreen
import com.ivianuu.essentials.ui.dialog.TextInputScreen
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide class KeycodeActionFactory(
  private val rootCommandRunner: ActionRootCommandRunner,
  private val resources: Resources
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val keycode = id.removePrefix(BASE_ID)
    return Action<ActionId>(
      id = id,
      title = resources(R.string.es_action_keycode_suffix, keycode),
      icon = staticActionIcon(R.drawable.es_ic_keyboard),
      permissions = listOf(typeKeyOf<ActionRootPermission>()),
      enabled = true
    )
  }

  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val keycode = id.removePrefix(BASE_ID)
    return ActionExecutor<ActionId> { rootCommandRunner("input keyevent $keycode") }
  }
}

@Provide class KeycodeActionPickerDelegate(
  private val navigator: Navigator,
  private val resources: Resources
) : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID
  override val title: String
    get() = resources(R.string.es_action_keycode)
  override val icon: @Composable () -> Unit
    get() = { Icon(R.drawable.es_ic_keyboard) }

  override suspend fun pickAction(): ActionPickerScreen.Result? {
    val keycode = navigator.push(
      TextInputScreen(
        label = resources(R.string.es_keycode_label),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        predicate = { it.isNotEmpty() }
      )
    )?.toIntOrNull() ?: return null

    return ActionPickerScreen.Result.Action("$BASE_ID$keycode")
  }
}

private const val BASE_ID = "keycode$ACTION_DELIMITER"
