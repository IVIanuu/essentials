/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.apppicker.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.gestures.action.ui.picker.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

@Provide class AppActionFactory(
  private val actionIntentSender: ActionIntentSender,
  private val appRepository: AppRepository,
  private val packageManager: PackageManager,
  private val RP: ResourceProvider
) : ActionFactory {
  override suspend fun handles(id: String): Boolean = id.startsWith(BASE_ID)

  override suspend fun createAction(id: String): Action<*> {
    val packageName = id.removePrefix(BASE_ID)
      .split(ACTION_DELIMITER)[0]
    return Action<ActionId>(
      id = id,
      title = appRepository.appInfo(packageName).first()?.appName ?: loadResource(R.string.es_unknown_action_name),
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
      actionIntentSender(
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
  private val RP: ResourceProvider
) : ActionPickerDelegate {
  override val baseId: String
    get() = BASE_ID
  override val title: String
    get() = loadResource(R.string.es_action_app)
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
