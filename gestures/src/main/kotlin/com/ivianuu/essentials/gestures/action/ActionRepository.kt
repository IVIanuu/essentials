/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import arrow.core.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.gestures.action.actions.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide class ActionRepository(
  private val actions: Map<String, () -> Action<*>>,
  private val actionFactories: List<() -> ActionFactory>,
  private val actionsExecutors: Map<String, () -> ActionExecutor<*>>,
  private val actionSettings: Map<String, () -> @ActionSettingsScreen<ActionId> Screen<Unit>>,
  private val actionPickerDelegates: List<() -> ActionPickerDelegate>,
  private val appConfig: AppConfig,
  private val coroutineContexts: CoroutineContexts,
  private val deviceScreenManager: DeviceScreenManager,
  private val logger: Logger,
  private val permissionManager: PermissionManager,
  private val systemDialogController: SystemDialogController,
  private val toaster: Toaster
) {
  suspend fun getAllActions() = withContext(coroutineContexts.computation) {
    actions.values.map { it() }
  }

  suspend fun getAction(id: String) = withContext(coroutineContexts.computation) {
    catch {
      actions[id]
        ?.invoke()
        ?: actionFactories
          .map { it() }
          .firstNotNullOfOrNull { it.createAction(id) }
    }
      .printErrors()
      .getOrNull()
      ?: Action(
        id = "error",
        title = RECONFIGURE_ACTION_MESSAGE,
        icon = { Icon(Icons.Default.Error, null) }
      )
  }

  suspend fun getActionExecutor(id: String) = withContext(coroutineContexts.computation) {
    catch {
      actionsExecutors[id]
        ?.invoke()
        ?: actionFactories
          .map { it() }
          .firstNotNullOfOrNull { it.createExecutor(id) }
    }.getOrNull()
      ?: ActionExecutor { toaster.toast(RECONFIGURE_ACTION_MESSAGE) }
  }

  suspend fun getActionSettingsKey(id: String) =
    withContext(coroutineContexts.computation) { actionSettings[id]?.invoke() }

  suspend fun getActionPickerDelegates() =
    withContext(coroutineContexts.computation) { actionPickerDelegates.map { it() } }

  suspend fun executeAction(id: String): Boolean = withContext(coroutineContexts.computation) {
    catch {
      logger.d { "execute $id" }
      val action = getAction(id)

      // check permissions
      if (!permissionManager.permissionState(action.permissions).first()) {
        logger.d { "didn't had permissions for $id ${action.permissions}" }
        deviceScreenManager.unlockScreen()
        permissionManager.requestPermissions(action.permissions)
        return@catch false
      }

      if (action.turnScreenOn && !deviceScreenManager.turnScreenOn()) {
        logger.d { "couldn't turn screen on for $id" }
        return@catch false
      }

      // unlock screen
      if (action.unlockScreen && !deviceScreenManager.unlockScreen()) {
        logger.d { "couldn't unlock screen for $id" }
        return@catch false
      }

      // close system dialogs
      if (action.closeSystemDialogs &&
        (appConfig.sdk < 31 ||
            permissionManager.permissionState(listOf(ActionAccessibilityPermission::class)).first()))
        systemDialogController.closeSystemDialogs()

      logger.d { "fire $id" }

      // fire
      getActionExecutor(id).execute()
      return@catch true
    }.onLeft {
      it.printStackTrace()
      toaster.toast("Failed to execute action $id!")
    }.getOrElse { false }
  }

  companion object {
    private const val RECONFIGURE_ACTION_MESSAGE = "Error please reconfigure this action"
  }
}
