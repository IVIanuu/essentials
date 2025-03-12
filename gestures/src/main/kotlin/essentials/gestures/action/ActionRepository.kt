/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.util.fastMap
import arrow.core.*
import essentials.*
import essentials.coroutines.*
import essentials.gestures.action.actions.*
import essentials.logging.*
import essentials.permission.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Stable @Provide class ActionRepository(
  private val actions: Map<String, () -> Action<*>>,
  private val actionFactories: List<() -> ActionFactory>,
  private val actionsExecutors: Map<String, () -> ActionExecutor<*>>,
  private val actionSettings: Map<String, () -> ActionSettingsScreen<ActionId>>,
  private val actionPickerDelegates: List<() -> ActionPickerDelegate>,
  private val appConfig: AppConfig,
  private val coroutineContexts: CoroutineContexts,
  private val deviceScreenManager: DeviceScreenManager,
  private val logger: Logger,
  private val permissionManager: PermissionManager,
  private val systemDialogController: SystemDialogController,
  private val showToast: showToast
) {
  suspend fun getAllActions() = withContext(coroutineContexts.computation) {
    actions.values.map { it() }
  }

  suspend fun getAction(id: String) = withContext(coroutineContexts.computation) {
    catch {
      actions[id]
        ?.invoke()
        ?: actionFactories
          .fastMap { it() }
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
          .fastMap { it() }
          .firstNotNullOfOrNull { it.createExecutor(id) }
    }.getOrNull()
      ?: ActionExecutor { showToast(RECONFIGURE_ACTION_MESSAGE) }
  }

  suspend fun getActionSettingsKey(id: String) =
    withContext(coroutineContexts.computation) { actionSettings[id]?.invoke() }

  suspend fun getActionPickerDelegates() =
    withContext(coroutineContexts.computation) { actionPickerDelegates.fastMap { it() } }

  suspend fun executeAction(id: String): Boolean = withContext(coroutineContexts.computation) {
    catch {
      logger.d { "execute $id" }
      val action = getAction(id)

      // check permissions
      if (!permissionManager.permissionState(action.permissions).first()) {
        logger.d { "didn't had permissions for $id ${action.permissions}" }
        deviceScreenManager.unlockScreen()
        permissionManager.ensurePermissions(action.permissions)
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
      showToast("Failed to execute action $id!")
    }.getOrElse { false }
  }

  companion object {
    private const val RECONFIGURE_ACTION_MESSAGE = "Error please reconfigure this action"
  }
}
