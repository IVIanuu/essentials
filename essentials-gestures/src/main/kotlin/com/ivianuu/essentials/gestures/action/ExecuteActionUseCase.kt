/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action

import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.CloseSystemDialogsUseCase
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.unlock.ScreenActivator
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

fun interface ExecuteActionUseCase {
  suspend fun executeAction(id: String): Result<Boolean, Throwable>
}

context(
ActionRepository,
CloseSystemDialogsUseCase,
Logger,
PermissionManager,
ScreenActivator,
ScreenUnlocker,
ToastContext)
    @Provide fun executeActionUseCase(coroutineContext: DefaultContext) =
  ExecuteActionUseCase { id ->
    withContext(coroutineContext) {
      catch {
        log { "execute $id" }
        val action = getAction(id)

        // check permissions
        if (!permissionState(action.permissions).first()) {
          log { "didn't had permissions for $id ${action.permissions}" }
          unlockScreen()
          requestPermissions(action.permissions)
        return@catch false
      }

        if (action.turnScreenOn && !activateScreen()) {
          log { "couldn't turn screen on for $id" }
          return@catch false
        }

      // unlock screen
        if (action.unlockScreen && !unlockScreen()) {
          log { "couldn't unlock screen for $id" }
          return@catch false
        }

      // close system dialogs
      if (action.closeSystemDialogs)
        closeSystemDialogs()

      log { "fire $id" }

      // fire
      getActionExecutor(id)()
      return@catch true
    }.onFailure {
      it.printStackTrace()
      showToast(R.string.es_action_execution_failed, id)
    }
  }
}
