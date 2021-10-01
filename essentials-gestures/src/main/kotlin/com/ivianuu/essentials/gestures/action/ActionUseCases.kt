/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action

import android.content.Intent
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.unlock.ScreenActivator
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.DefaultDispatcher
import kotlinx.coroutines.withContext

typealias ExecuteActionUseCase = suspend (String) -> Result<Boolean, Throwable>

@Provide fun executeActionUseCase(
  context: AppContext,
  dispatcher: DefaultDispatcher,
  logger: Logger,
  permissionRequester: PermissionRequester,
  repository: ActionRepository,
  screenActivator: ScreenActivator,
  screenUnlocker: ScreenUnlocker,
  rp: ResourceProvider,
  toaster: Toaster
): ExecuteActionUseCase = { key ->
  withContext(dispatcher) {
    catch {
      log { "execute $key" }
      val action = repository.getAction(key)!!

      // check permissions
      if (!permissionRequester(action.permissions)) {
        log { "couldn't get permissions for $key" }
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
        context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))

      log { "fire $key" }

      // fire
      repository.getActionExecutor(key)!!()
      return@catch true
    }.onFailure {
      it.printStackTrace()
      showToast(R.string.es_action_execution_failed, key)
    }
  }
}
