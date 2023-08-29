/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.CloseSystemDialogsUseCase
import com.ivianuu.essentials.gestures.action.actions.staticActionIcon
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.getOrElse
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.unlock.ScreenActivator
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface ActionRepository {
  suspend fun getAllActions(): List<Action<*>>

  suspend fun getAction(id: String): Action<*>

  suspend fun getActionExecutor(id: String): ActionExecutor<*>

  suspend fun getActionSettingsKey(id: String): Screen<Unit>?

  suspend fun getActionPickerDelegates(): List<ActionPickerDelegate>

  suspend fun executeAction(id: String): Boolean
}

@Provide class ActionRepositoryImpl(
  private val actions: () -> Map<String, () -> Action<*>>,
  private val actionFactories: () -> List<() -> ActionFactory>,
  private val actionsExecutors: () -> Map<String, () -> ActionExecutor<*>>,
  private val actionSettings: () -> Map<String, () -> @ActionSettingsKey<ActionId> Screen<Unit>>,
  private val actionPickerDelegates: () -> List<() -> ActionPickerDelegate>,
  private val closeSystemDialogs: CloseSystemDialogsUseCase,
  private val coroutineContexts: CoroutineContexts,
  private val logger: Logger,
  private val permissionManager: PermissionManager,
  private val screenActivator: ScreenActivator,
  private val screenUnlocker: ScreenUnlocker,
  private val resources: Resources,
  private val toaster: Toaster
) : ActionRepository {
  override suspend fun getAllActions() = withContext(coroutineContexts.computation) {
    actions().values.map { it() }
  }

  override suspend fun getAction(id: String) = withContext(coroutineContexts.computation) {
    actions()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createAction(id)
      ?: Action(
        id = "error",
        title = resources(R.string.es_error_action),
        icon = staticActionIcon(R.drawable.es_ic_error)
      )
  }

  override suspend fun getActionExecutor(id: String) = withContext(coroutineContexts.computation) {
    actionsExecutors()[id]
      ?.invoke()
      ?: actionFactories()
        .asSequence()
        .map { it() }
        .firstOrNull { it.handles(id) }
        ?.createExecutor(id)
      ?: ActionExecutor {
        toaster(R.string.es_error_action)
      }
  }

  override suspend fun getActionSettingsKey(id: String) =
    withContext(coroutineContexts.computation) { actionSettings()[id]?.invoke() }

  override suspend fun getActionPickerDelegates() =
    withContext(coroutineContexts.computation) { actionPickerDelegates().map { it() } }

  override suspend fun executeAction(id: String): Boolean =
    withContext(coroutineContexts.computation) {
      catch {
        logger.log { "execute $id" }
        val action = getAction(id)

        // check permissions
        if (!permissionManager.permissionState(action.permissions).first()) {
          logger.log { "didn't had permissions for $id ${action.permissions}" }
          screenUnlocker()
          permissionManager.requestPermissions(action.permissions)
          return@catch false
        }

        if (action.turnScreenOn && !screenActivator()) {
          logger.log { "couldn't turn screen on for $id" }
          return@catch false
        }

        // unlock screen
        if (action.unlockScreen && !screenUnlocker()) {
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
      }.onFailure {
        it.printStackTrace()
        toaster(R.string.es_action_execution_failed, id)
      }.getOrElse { false }
    }
}