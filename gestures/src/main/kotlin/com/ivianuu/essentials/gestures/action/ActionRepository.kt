/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import arrow.core.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.actions.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide class ActionRepository(
  private val actions: () -> Map<String, () -> Action<*>>,
  private val actionFactories: () -> List<() -> ActionFactory>,
  private val actionsExecutors: () -> Map<String, () -> ActionExecutor<*>>,
  private val actionSettings: () -> Map<String, () -> @ActionSettingsKey<ActionId> Screen<Unit>>,
  private val actionPickerDelegates: () -> List<() -> ActionPickerDelegate>,
  private val closeSystemDialogs: CloseSystemDialogsUseCase,
  private val coroutineContexts: CoroutineContexts,
  private val deviceScreenManager: DeviceScreenManager,
  private val logger: Logger,
  private val permissionManager: PermissionManager,
  private val resources: Resources,
  private val toaster: Toaster
) {
  suspend fun getAllActions() = withContext(coroutineContexts.computation) {
    actions().values.map { it() }
  }

  suspend fun getAction(id: String) = withContext(coroutineContexts.computation) {
    actions()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createAction(id)
      ?: Action(
        id = "error",
        title = resources(R.string.error_action),
        icon = staticActionIcon(R.drawable.ic_error)
      )
  }

  suspend fun getActionExecutor(id: String) = withContext(coroutineContexts.computation) {
    actionsExecutors()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createExecutor(id)
      ?: ActionExecutor {
        toaster(R.string.error_action)
      }
  }

  suspend fun getActionSettingsKey(id: String) =
    withContext(coroutineContexts.computation) { actionSettings()[id]?.invoke() }

  suspend fun getActionPickerDelegates() =
    withContext(coroutineContexts.computation) { actionPickerDelegates().map { it() } }

  suspend fun executeAction(id: String): Boolean =
    withContext(coroutineContexts.computation) {
      catch {
        logger.log { "execute $id" }
        val action = getAction(id)

        // check permissions
        if (!permissionManager.permissionState(action.permissions).first()) {
          logger.log { "didn't had permissions for $id ${action.permissions}" }
          deviceScreenManager.unlockScreen()
          permissionManager.requestPermissions(action.permissions)
          return@catch false
        }

        if (action.turnScreenOn && !deviceScreenManager.turnScreenOn()) {
          logger.log { "couldn't turn screen on for $id" }
          return@catch false
        }

        // unlock screen
        if (action.unlockScreen && !deviceScreenManager.unlockScreen()) {
          logger.log { "couldn't unlock screen for $id" }
          return@catch false
        }

        // close system dialogs
        if (action.closeSystemDialogs)
          closeSystemDialogs()

        logger.log { "fire $id" }

        // fire
        getActionExecutor(id)()
        return@catch true
      }.onLeft {
        it.printStackTrace()
        toaster(R.string.action_execution_failed, id)
      }.getOrElse { false }
    }
}
