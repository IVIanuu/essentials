/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide class AppActionFactory(
  private val appRepository: AppRepository,
  private val intentSender: ActionIntentSender,
  private val packageManager: PackageManager
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val packageName = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)[0]
    return Action<ActionId>(
      id = id,
      title = appRepository.appInfo(packageName).first()!!.appName,
      unlockScreen = true,
      closeSystemDialogs = true,
      enabled = true,
      permissions = listOf(ActionSystemOverlayPermission::class),
      icon = staticActionImage(AppIcon(packageName))
    )
  }

  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val packageName = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)
      .first()
    return ActionExecutor<ActionId> {
      intentSender(
        packageManager.getLaunchIntentForPackage(packageName)!!,
        null
      )
    }
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
