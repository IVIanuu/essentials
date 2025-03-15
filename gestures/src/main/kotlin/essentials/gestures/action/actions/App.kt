/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.pm.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import coil.compose.*
import essentials.apps.*
import essentials.gestures.action.*
import essentials.gestures.action.ui.*
import essentials.ui.navigation.*
import injekt.*

@Provide class AppActionFactory(
  private val getAppInfo: suspend (String) -> AppInfo?,
  private val packageManager: PackageManager,
  private val sendIntent: sendActionIntent
) : ActionFactory {
  override suspend fun createAction(id: String): Action<*>? {
    if (!id.startsWith(BASE_ID)) return null
    val packageName = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)
      .first()
    return Action<ActionId>(
      id = id,
      title = getAppInfo(packageName)!!.appName,
      unlockScreen = true,
      closeSystemDialogs = true,
      enabled = true,
      permissions = listOf(ActionSystemOverlayPermission::class),
      icon = { AsyncImage(AppIcon(packageName), null) }
    )
  }

  override suspend fun execute(id: String): ExecuteActionResult<*>? {
    if (!id.startsWith(BASE_ID)) return null
    val packageName = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)
      .first()
    sendIntent(
      packageManager.getLaunchIntentForPackage(packageName)!!,
      null
    )
    return ExecuteActionResult
  }
}

@Provide class AppActionPickerDelegate(
  private val launchableAppPredicate: LaunchableAppPredicate
) : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID
  override val title: String
    get() = "App"
  override val icon: @Composable () -> Unit
    get() = { Icon(Icons.Default.Apps, null) }

  override suspend fun pickAction(navigator: Navigator): ActionPickerScreen.Result? {
    val app = navigator.push(AppPickerScreen(launchableAppPredicate)) ?: return null
    return ActionPickerScreen.Result.Action("$BASE_ID${app.packageName}$ACTION_DELIMITER")
  }
}

private const val BASE_ID = "app$ACTION_DELIMITER"
