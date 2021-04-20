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
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.unlock.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

typealias ExecuteActionUseCase = suspend (String) -> com.github.michaelbull.result.Result<Boolean, Throwable>

@Given
fun executeActionUseCase(
    @Given dispatcher: DefaultDispatcher,
    @Given getAction: GetActionUseCase,
    @Given getActionExecutor: GetActionExecutorUseCase,
    @Given logger: Logger,
    @Given permissionRequester: PermissionRequester,
    @Given screenUnlocker: ScreenUnlocker,
    @Given toaster: Toaster
): ExecuteActionUseCase = { key ->
    withContext(dispatcher) {
        catch {
            logger.d { "execute $key" }
            val action = getAction(key)!!

            // check permissions
            if (!permissionRequester(action.permissions)) {
                logger.d { "couldn't get permissions for $key" }
                return@catch false
            }

            // unlock screen
            if (action.unlockScreen && !screenUnlocker()) {
                logger.d { "couldn't unlock screen for $key" }
                return@catch false
            }

            logger.d { "fire $key" }

            // fire
            getActionExecutor(key)!!()
            return@catch true
        }.onFailure {
            it.printStackTrace()
            toaster("Failed to execute '$key'") // todo res
        }
    }
}

typealias GetAllActionsUseCase = suspend () -> List<Action<*>>

@Given
fun getAllActionsUseCase(
    @Given actions: Map<String, () -> Action<*>>,
    @Given dispatcher: DefaultDispatcher
): GetAllActionsUseCase = {
    withContext(dispatcher) { actions.values.map { it() } }
}

typealias GetActionUseCase = suspend (String) -> Action<*>?

@Given
fun getActionUseCase(
    @Given actions: Map<String, () -> Action<*>>,
    @Given actionFactories: () -> Set<() -> ActionFactory>,
    @Given dispatcher: DefaultDispatcher
): GetActionUseCase = { key ->
    withContext(dispatcher) {
        actions[key]
            ?.invoke()
            ?: actionFactories()
                .asSequence()
                .map { it() }
                .firstOrNull { it.handles(key) }
                ?.createAction(key)
    }
}

typealias GetActionExecutorUseCase = suspend (String) -> ActionExecutor<*>?

@Given
fun getActionExecutorUseCase(
    @Given actionsExecutors: Map<String, () -> ActionExecutor<*>>,
    @Given actionFactories: () -> Set<() -> ActionFactory>,
    @Given dispatcher: DefaultDispatcher
): GetActionExecutorUseCase = { key ->
    withContext(dispatcher) {
        actionsExecutors[key]
            ?.invoke()
            ?: actionFactories()
                .asSequence()
                .map { it() }
                .firstOrNull { it.handles(key) }
                ?.createExecutor(key)
            ?: error("Unsupported action key $key")
    }
}

typealias GetActionSettingsKeyUseCase = suspend (String) -> Key<Nothing>?

@Given
fun getActionSettingsKeyUseCase(
    @Given actionSettings: Map<String, () -> ActionSettingsKey<*>>,
    @Given dispatcher: DefaultDispatcher
): GetActionSettingsKeyUseCase = { key ->
    withContext(dispatcher) { actionSettings[key]?.invoke() }
}

typealias GetActionPickerDelegatesUseCase = suspend () -> List<ActionPickerDelegate>

@Given
fun getActionPickerDelegatesUseCase(
    @Given actionPickerDelegates: Set<() -> ActionPickerDelegate>,
    @Given dispatcher: DefaultDispatcher
): GetActionPickerDelegatesUseCase = {
    withContext(dispatcher) { actionPickerDelegates.map { it() } }
}
