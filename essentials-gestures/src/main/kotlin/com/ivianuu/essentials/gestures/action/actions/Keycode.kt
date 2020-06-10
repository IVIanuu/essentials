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
import com.ivianuu.essentials.gestures.action.actionFactory
import com.ivianuu.essentials.gestures.action.actionPickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.dialog.TextInputRoute
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn

@Module
private fun KeycodeModule() {
    installIn<ApplicationComponent>()
    actionFactory<KeycodeActionFactory>()
    actionPickerDelegate<KeycodeActionPickerDelegate>()
}

@Transient
internal class KeycodeActionFactory(
    private val permissions: ActionPermissions,
    private val resourceProvider: ResourceProvider,
    private val rootActionExecutorProvider: @Provider (String) -> RootActionExecutor
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        val keycode = key.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            key = key,
            title = resourceProvider.getString(R.string.es_action_keycode_suffix, keycode),
            iconProvider = SingleActionIconProvider(Icons.Default.Keyboard),
            permissions = listOf(permissions.root),
            executor = rootActionExecutorProvider("input keyevent $keycode"),
            unlockScreen = false,
            enabled = true
        )
    }
}

@Transient
internal class KeycodeActionPickerDelegate(
    private val resourceProvider: ResourceProvider
) : ActionPickerDelegate {
    override val title: String
        get() = resourceProvider.getString(R.string.es_action_keycode)
    override val icon: @Composable () -> Unit
        get() = { Icon(Icons.Default.Keyboard) }

    override suspend fun getResult(navigator: Navigator): ActionPickerResult? {
        val keycode =
            navigator.push<String>(
                TextInputRoute(
                    title = resourceProvider.getString(R.string.es_keycode_picker_title),
                    hint = resourceProvider.getString(R.string.es_keycode_input_hint),
                    keyboardType = KeyboardType.Number,
                    allowEmpty = false
                )
            ).await()?.toIntOrNull() ?: return null

        return ActionPickerResult.Action("$ACTION_KEY_PREFIX$keycode")
    }
}

private const val ACTION_KEY_PREFIX = "keycode=:="
