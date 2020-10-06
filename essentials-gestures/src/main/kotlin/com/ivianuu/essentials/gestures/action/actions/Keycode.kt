package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionFactoryBinding
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionPickerDelegateBinding
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.TextInputRoute
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.Resources

@ActionFactoryBinding
class KeycodeActionFactory(
    private val permissions: ActionPermissions,
    private val runRootCommand: runRootCommand,
    private val resources: Resources,
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        val keycode = key.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            key = key,
            title = resources.getString(R.string.es_action_keycode_suffix, keycode),
            icon = singleActionIcon(R.drawable.es_ic_keyboard),
            permissions = listOf(permissions.root),
            unlockScreen = false,
            enabled = true,
            execute = { runRootCommand("input keyevent $keycode") }
        )
    }
}

@ActionPickerDelegateBinding
class KeycodeActionPickerDelegate(
    private val navigator: Navigator,
    private val resources: Resources,
) : ActionPickerDelegate {
    override val title: String
        get() = resources.getString(R.string.es_action_keycode)
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
