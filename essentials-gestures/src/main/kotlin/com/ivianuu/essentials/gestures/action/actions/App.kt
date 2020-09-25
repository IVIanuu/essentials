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
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.GivenActionFactory
import com.ivianuu.essentials.gestures.action.GivenActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.given

@GivenActionFactory
class AppActionFactory : ActionFactory {
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
                given<PackageManager>().getLaunchIntentForPackage(
                    packageName
                )!!.send()
            }
        )
    }
}

@GivenActionPickerDelegate
class AppActionPickerDelegate : ActionPickerDelegate {
    override val title: String
        get() = Resources.getString(R.string.es_action_app)
    override val icon: @Composable () -> Unit
        get() = { Icon(vectorResource(R.drawable.es_ic_apps)) }

    override suspend fun getResult(): ActionPickerResult? {
        val app = navigator.push<AppInfo> {
            AppPickerPage(appFilter = given<LaunchableAppFilter>())
        } ?: return null
        return ActionPickerResult.Action("$ACTION_KEY_PREFIX${app.packageName}")
    }
}

private const val ACTION_KEY_PREFIX = "app=:="
