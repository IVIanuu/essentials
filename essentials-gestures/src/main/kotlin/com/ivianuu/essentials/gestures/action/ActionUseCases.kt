/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.EsResult
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.CloseSystemDialogsUseCase
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionStateFactory
import com.ivianuu.essentials.unlock.ScreenActivator
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

fun interface ExecuteActionUseCase : suspend (String) -> EsResult<Boolean, Throwable>

@Provide fun executeActionUseCase(
  closeSystemDialogs: CloseSystemDialogsUseCase,
  coroutineContext: DefaultContext,
  permissionRequester: PermissionRequester,
  permissionStateFactory: PermissionStateFactory,
  repository: ActionRepository,
  screenActivator: ScreenActivator,
  screenUnlocker: ScreenUnlocker,
  L: Logger,
  RP: ResourceProvider,
  T: Toaster
) = ExecuteActionUseCase { key ->
  withContext(coroutineContext) {
    catch {
      log { "execute $key" }
      val action = repository.getAction(key)

      // check permissions
      if (!permissionStateFactory(action.permissions).first()) {
        log { "didn't had permissions for $key ${action.permissions}" }
        screenUnlocker()
        permissionRequester(action.permissions)
        return@catch false
      }

      if (action.turnScreenOn && !screenActivator()) {
        log { "couldn't turn screen on for $key" }
        return@catch false
      }

      // unlock screen
      if (action.unlockScreen && !screenUnlocker()) {
        log { "couldn't unlock screen for $key" }
        return@catch false
      }

      // close system dialogs
      if (action.closeSystemDialogs)
        closeSystemDialogs()

      log { "fire $key" }

      // fire
      repository.getActionExecutor(key)()
      return@catch true
    }.onFailure {
      it.printStackTrace()
      showToast(R.string.es_action_execution_failed, key)
    }
  }
}
