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

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.getAppInfo
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerKey
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionFactoryBinding
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionPickerDelegateBinding
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.ui.navigation.pushKeyForResult
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given

@ActionFactoryBinding
@Given
class AppActionFactory(
    @Given private val getAppInfo: getAppInfo,
    @Given private val packageManager: PackageManager,
    @Given private val sendIntent: sendIntent,
) : ActionFactory {
    override suspend fun handles(id: String): Boolean = id.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(id: String): Action {
        val packageName = id.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            id = id,
            title = getAppInfo(packageName).appName,
            unlockScreen = true,
            enabled = true,
            icon = coilActionIcon(AppIcon(packageName))
        )
    }

    override suspend fun createExecutor(id: String): ActionExecutor {
        val packageName = id.removePrefix(ACTION_KEY_PREFIX)
        return {
            sendIntent(
                packageManager.getLaunchIntentForPackage(
                    packageName
                )!!
            )
        }
    }
}

@ActionPickerDelegateBinding
@Given
class AppActionPickerDelegate(
    @Given private val launchableAppFilter: LaunchableAppFilter,
    @Given private val pickApp: pushKeyForResult<AppPickerKey, AppInfo>,
    @Given private val stringResource: stringResource,
) : ActionPickerDelegate {
    override val title: String
        get() = stringResource(R.string.es_action_app)
    override val icon: @Composable () -> Unit
        get() = { Icon(R.drawable.es_ic_apps) }

    override suspend fun getResult(): ActionPickerResult? {
        val app = pickApp(AppPickerKey(launchableAppFilter)) ?: return null
        return ActionPickerResult.Action("$ACTION_KEY_PREFIX${app.packageName}")
    }
}

private const val ACTION_KEY_PREFIX = "app=:="
