/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.CloseSystemDialogsUseCase
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.invoke
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.unlock.ScreenActivator
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.invoke
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

fun interface ExecuteActionUseCase : suspend (String) -> Result<Boolean, Throwable>

@Provide fun executeActionUseCase(
  closeSystemDialogs: CloseSystemDialogsUseCase,
  coroutineContext: DefaultContext,
  logger: Logger,
  permissionManager: PermissionManager,
  repository: ActionRepository,
  resourceProvider: ResourceProvider,
  screenActivator: ScreenActivator,
  screenUnlocker: ScreenUnlocker,
  toaster: Toaster
) = ExecuteActionUseCase { id ->
  withContext(coroutineContext) {
    catch {
      logger { "execute $id" }
      val action = repository.getAction(id)

      // check permissions
      if (!permissionManager.permissionState(action.permissions).first()) {
        logger { "didn't had permissions for $id ${action.permissions}" }
        screenUnlocker()
        permissionManager.requestPermissions(action.permissions)
        return@catch false
      }

      if (action.turnScreenOn && !screenActivator()) {
        logger { "couldn't turn screen on for $id" }
        return@catch false
      }

      // unlock screen
      if (action.unlockScreen && !screenUnlocker()) {
        logger { "couldn't unlock screen for $id" }
        return@catch false
      }

      // close system dialogs
      if (action.closeSystemDialogs)
        closeSystemDialogs()

      logger { "fire $id" }

      // fire
      repository.getActionExecutor(id)()
      return@catch true
    }.onFailure {
      it.printStackTrace()
      toaster(R.string.es_action_execution_failed, id)
    }
  }
}
