package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import androidx.ui.input.KeyboardType
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Keyboard
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.bindActionFactoryIntoSet
import com.ivianuu.essentials.gestures.action.bindActionPickerDelegateIntoSet
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.dialog.TextInputRoute
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf

internal fun ComponentBuilder.keycodeAction() {
    bindActionFactoryIntoSet<KeycodeActionFactory>()
    bindActionPickerDelegateIntoSet<KeycodeActionPickerDelegate>()
}

@Factory
private class KeycodeActionFactory(
    private val actionPermissions: ActionPermissions,
    private val resourceProvider: ResourceProvider,
    private val rootActionExecutorProvider: Provider<RootActionExecutor>
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        val keycode = key.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            key = key,
            title = resourceProvider.getString(R.string.es_action_keycode_suffix, keycode),
            iconProvider = SingleActionIconProvider(Icons.Default.Keyboard),
            permissions = listOf(actionPermissions.root),
            executor = rootActionExecutorProvider(parameters = parametersOf("input keyevent $keycode")),
            unlockScreen = false
        )
    }
}

@Factory
private class KeycodeActionPickerDelegate(
    private val resourceProvider: ResourceProvider
) : ActionPickerDelegate {
    override val title: String
        get() = resourceProvider.getString(R.string.es_action_keycode)
    override val icon: @Composable () -> Unit
        get() = { Icon(Icons.Default.Keyboard) }

    override suspend fun getResult(navigator: NavigatorState): ActionPickerResult? {
        val keycode =
            navigator.push<String>(
            TextInputRoute(
                title = resourceProvider.getString(R.string.es_keycode_picker_title),
                hint = resourceProvider.getString(R.string.es_keycode_input_hint),
                keyboardType = KeyboardType.Number,
                allowEmpty = false
            )
        )?.toIntOrNull() ?: return null

        return ActionPickerResult.Action("$ACTION_KEY_PREFIX$keycode")
    }
}

private const val ACTION_KEY_PREFIX = "keycode=:="
