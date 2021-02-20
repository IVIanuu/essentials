/*
 * Copyright 2020 Manuel Wrage
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

import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.result.Result
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.unlock.ScreenUnlocker
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import kotlinx.coroutines.withContext

typealias executeAction = suspend (String) -> Result<Boolean, Throwable>

@Given
fun executeAction(
    @Given actionRepository: ActionRepository,
    @Given defaultDispatcher: DefaultDispatcher,
    @Given logger: Logger,
    @Given permissionRequester: PermissionRequester,
    @Given screenUnlocker: ScreenUnlocker,
    @Given toaster: Toaster
): executeAction = { key ->
    withContext(defaultDispatcher) {
        runKatching {
            logger.d { "execute $key" }
            val action = actionRepository.getAction(key)

            // check permissions
            if (!permissionRequester(action.permissions)) {
                logger.d { "couldn't get permissions for $key" }
                return@runKatching false
            }

            // unlock screen
            if (action.unlockScreen && !screenUnlocker()) {
                logger.d { "couldn't unlock screen for $key" }
                return@runKatching false
            }

            logger.d { "fire $key" }

            // fire
            actionRepository.getActionExecutor(key)()
            return@runKatching true
        }.onFailure {
            it.printStackTrace()
            toaster.showToast("Failed to execute '$key'") // todo res
        }
    }
}
