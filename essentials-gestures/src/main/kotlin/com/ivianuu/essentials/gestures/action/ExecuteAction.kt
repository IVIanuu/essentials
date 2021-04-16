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

import com.github.michaelbull.result.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.unlock.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

typealias executeAction = suspend (String) -> Result<Boolean, Throwable>

@Given
fun executeAction(
    @Given dispatcher: DefaultDispatcher,
    @Given getAction: GetActionUseCase,
    @Given getActionExecutor: GetActionExecutorUseCase,
    @Given logger: Logger,
    @Given permissionRequester: PermissionRequester,
    @Given screenUnlocker: ScreenUnlocker,
    @Given toaster: Toaster
): executeAction = { key ->
    withContext(dispatcher) {
        runCatching {
            logger.d { "execute $key" }
            val action = getAction(key)!!

            // check permissions
            if (!permissionRequester(action.permissions)) {
                logger.d { "couldn't get permissions for $key" }
                return@runCatching false
            }

            // unlock screen
            if (action.unlockScreen && !screenUnlocker()) {
                logger.d { "couldn't unlock screen for $key" }
                return@runCatching false
            }

            logger.d { "fire $key" }

            // fire
            getActionExecutor(key)!!()
            return@runCatching true
        }.onFailure {
            it.printStackTrace()
            toaster("Failed to execute '$key'") // todo res
        }
    }
}
