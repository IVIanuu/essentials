package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.PackageManager
import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.getAppInfo
import com.ivianuu.essentials.apps.ui.AppPickerPage
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionFactoryBinding
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionPickerDelegateBinding
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.stringResource

@ActionFactoryBinding
class AppActionFactory(
    private val getAppInfo: getAppInfo,
    private val packageManager: PackageManager,
    private val sendIntent: sendIntent,
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        val packageName = key.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            key = key,
            title = getAppInfo(packageName).packageName,
            unlockScreen = true,
            enabled = true,
            icon = coilActionIcon(AppIcon(packageName)),
            execute = {
                sendIntent(
                    packageManager.getLaunchIntentForPackage(
                        packageName
                    )!!
                )
            }
        )
    }
}

@ActionPickerDelegateBinding
class AppActionPickerDelegate(
    private val appPickerPage: AppPickerPage,
    private val launchableAppFilter: LaunchableAppFilter,
    private val navigator: Navigator,
    private val stringResource: stringResource,
) : ActionPickerDelegate {
    override val title: String
        get() = stringResource(R.string.es_action_app)
    override val icon: @Composable () -> Unit
        get() = { Icon(vectorResource(R.drawable.es_ic_apps)) }

    override suspend fun getResult(): ActionPickerResult? {
        val app = navigator.push<AppInfo> {
            appPickerPage(launchableAppFilter, null)
        } ?: return null
        return ActionPickerResult.Action("$ACTION_KEY_PREFIX${app.packageName}")
    }
}

private const val ACTION_KEY_PREFIX = "app=:="
