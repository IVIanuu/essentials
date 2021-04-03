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
import com.ivianuu.injekt.Given
import kotlinx.coroutines.withContext

interface ActionRepository {
    suspend fun getAllActions(): List<Action>

    suspend fun getAction(key: String): Action

    suspend fun getActionExecutor(key: String): ActionExecutor

    suspend fun getActionSettingsKey(key: String): ActionSettingsKey?

    suspend fun getActionPickerDelegates(): List<ActionPickerDelegate>
}

@Given
class ActionRepositoryImpl(
    @Given private val defaultDispatcher: DefaultDispatcher,
    @Given private val actions: Map<String, () -> Action> = emptyMap(),
    @Given private val actionFactories: () -> Set<ActionFactory> = { emptySet() },
    @Given private val actionsExecutors: Map<String, ActionExecutor> = emptyMap(),
    @Given private val actionPickerDelegates: Set<ActionPickerDelegate> = emptySet(),
    @Given private val actionSettings: Map<String, ActionSettingsKey> = emptyMap()
) : ActionRepository {
    override suspend fun getAllActions(): List<Action> = withContext(defaultDispatcher) {
        actions.values.map { it() }
    }

    override suspend fun getAction(key: String): Action = withContext(defaultDispatcher) {
        actions.toMap()[key]
            ?.invoke()
            ?: actionFactories()
                .firstOrNull { it.handles(key) }
                ?.createAction(key)
            ?: error("Unsupported action key $key")
    }

    override suspend fun getActionExecutor(key: String): ActionExecutor = withContext(defaultDispatcher) {
        actionsExecutors.toMap()[key]
            ?: actionFactories()
                .firstOrNull { it.handles(key) }
                ?.createExecutor(key)
            ?: error("Unsupported action key $key")
    }

    override suspend fun getActionSettingsKey(key: String): ActionSettingsKey? =
        withContext(defaultDispatcher) { actionSettings[key] }

    override suspend fun getActionPickerDelegates(): List<ActionPickerDelegate> =
        actionPickerDelegates.toList()
}
