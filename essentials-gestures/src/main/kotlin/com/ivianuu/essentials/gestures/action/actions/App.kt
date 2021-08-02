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

import android.content.pm.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.coil.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.apppicker.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

@Provide class AppActionFactory(
  private val actionIntentSender: ActionIntentSender,
  private val getAppInfo: GetAppInfoUseCase,
  private val packageManager: PackageManager,
  private val rp: ResourceProvider
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(ACTION_KEY_PREFIX)

  override suspend fun createAction(id: String): Action<*> {
    val packageName = id.removePrefix(ACTION_KEY_PREFIX)
    return Action<ActionId>(
      id = id,
      title = getAppInfo(packageName)?.appName
        ?: loadResource(R.string.es_unknown_action_name),
      unlockScreen = true,
      enabled = true,
      icon = coilActionIcon(AppIcon(packageName))
    )
  }

  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val packageName = id.removePrefix(ACTION_KEY_PREFIX)
    return {
      actionIntentSender(
        packageManager.getLaunchIntentForPackage(
          packageName
        )!!
      )
    }
  }
}

@Provide class AppActionPickerDelegate(
  private val launchableAppPredicate: LaunchableAppPredicate,
  private val navigator: Navigator,
  private val rp: ResourceProvider,
) : ActionPickerDelegate {
  override val title: String
    get() = loadResource(R.string.es_action_app)

  override val icon: @Composable () -> Unit = {
    Icon(painterResource(R.drawable.es_ic_apps), null)
  }

  override suspend fun pickAction(): ActionPickerKey.Result? {
    val app = navigator.push(AppPickerKey(launchableAppPredicate)) ?: return null
    return ActionPickerKey.Result.Action("$ACTION_KEY_PREFIX${app.packageName}")
  }
}

private const val ACTION_KEY_PREFIX = "app=:="
