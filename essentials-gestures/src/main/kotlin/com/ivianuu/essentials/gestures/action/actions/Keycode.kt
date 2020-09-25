package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.GivenActionFactory
import com.ivianuu.essentials.gestures.action.GivenActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.TextInputRoute
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.given

@GivenActionFactory
class KeycodeActionFactory : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        val keycode = key.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            key = key,
            title = Resources.getString(R.string.es_action_keycode_suffix, keycode),
            icon = singleActionIcon(R.drawable.es_ic_keyboard),
            permissions = listOf(given<ActionPermissions>().root),
            unlockScreen = false,
            enabled = true,
            execute = { runRootCommand("input keyevent $keycode") }
        )
    }
}

@GivenActionPickerDelegate
class KeycodeActionPickerDelegate : ActionPickerDelegate {
    override val title: String
        get() = Resources.getString(R.string.es_action_keycode)
    override val icon: @Composable () -> Unit
        get() = { Icon(vectorResource(R.drawable.es_ic_keyboard)) }

    override suspend fun getResult(): ActionPickerResult? {
        val keycode = navigator.push<String>(
            TextInputRoute(
                title = { Text(R.string.es_keycode_picker_title) },
                label = { Text(R.string.es_keycode_input_hint) },
                keyboardType = KeyboardType.Number,
                allowEmpty = false
            )
        )?.toIntOrNull() ?: return null

        return ActionPickerResult.Action("$ACTION_KEY_PREFIX$keycode")
    }
}

private const val ACTION_KEY_PREFIX = "keycode=:="
