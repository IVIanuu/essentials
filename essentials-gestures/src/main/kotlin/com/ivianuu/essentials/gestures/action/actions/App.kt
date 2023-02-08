/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.PackageManager
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.LaunchableAppPredicate
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerKey
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ACTION_DELIMITER
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionSystemOverlayPermission
import com.ivianuu.essentials.gestures.action.FloatingWindowActionsEnabled
import com.ivianuu.essentials.gestures.action.ui.FloatingWindowsPickerKey
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.first

@Provide class AppActionFactory(
  private val appRepository: AppRepository,
  private val intentSender: ActionIntentSender,
  private val packageManager: PackageManager,
  private val resources: Resources
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val packageName = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)[0]
    return Action<ActionId>(
      id = id,
      title = appRepository.appInfo(packageName).first()?.appName
        ?: resources(R.string.es_unknown_action_name),
      unlockScreen = true,
      closeSystemDialogs = true,
      enabled = true,
      permissions = listOf(typeKeyOf<ActionSystemOverlayPermission>()),
      icon = staticActionImage(AppIcon(packageName))
    )
  }

  override suspend fun createExecutor(id: String): ActionExecutor<*> {
    val (packageName, isFloating) = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)
      .let { it[0] to it[1].toBoolean() }
    return ActionExecutor<ActionId> {
      intentSender(
        packageManager.getLaunchIntentForPackage(packageName)!!,
        isFloating,
        null
      )
    }
  }
}

@Provide class AppActionPickerDelegate(
  private val floatingWindowActionsEnabled: FloatingWindowActionsEnabled,
  private val launchableAppPredicate: LaunchableAppPredicate,
  private val navigator: Navigator,
  private val resources: Resources
) : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID
  override val title: String
    get() = resources(R.string.es_action_app)
  override val icon: @Composable () -> Unit
    get() = { Icon(R.drawable.es_ic_apps) }

  override suspend fun pickAction(): ActionPickerKey.Result? {
    val app = navigator.push(AppPickerKey(launchableAppPredicate)) ?: return null
    val isFloating = floatingWindowActionsEnabled.value &&
        navigator.push(FloatingWindowsPickerKey(app.appName)) ?: return null
    return ActionPickerKey.Result.Action("$BASE_ID${app.packageName}$ACTION_DELIMITER$isFloating")
  }
}

private const val BASE_ID = "app$ACTION_DELIMITER"
