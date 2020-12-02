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

import androidx.compose.runtime.Composable
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.withContext

@FunBinding
suspend fun getAllActions(
    actions: Map<String, () -> Action>,
    defaultDispatcher: DefaultDispatcher,
): List<Action> = withContext(defaultDispatcher) {
    actions.values
        .map { it() }
}

@FunBinding
suspend fun getAction(
    actions: Map<String, () -> Action>,
    actionFactories: () -> Set<ActionFactory>,
    defaultDispatcher: DefaultDispatcher,
    @FunApi key: String,
): Action = withContext(defaultDispatcher) {
    actions[key]
        ?.invoke()
        ?: actionFactories()
            .firstOrNull { it.handles(key) }
            ?.createAction(key)
        ?: error("Unsupported action key $key")
}

@FunBinding
suspend fun getActionExecutor(
    actionsExecutors: Map<String, ActionExecutor>,
    actionFactories: () -> Set<ActionFactory>,
    defaultDispatcher: DefaultDispatcher,
    @FunApi key: String,
): ActionExecutor = withContext(defaultDispatcher) {
    actionsExecutors[key]
        ?: actionFactories()
            .firstOrNull { it.handles(key) }
            ?.createExecutor(key)
        ?: error("Unsupported action key $key")
}

@FunBinding
suspend fun getActionSettingsKey(
    actionSettings: Map<String, Key>,
    defaultDispatcher: DefaultDispatcher,
    @FunApi key: String,
): Key? = withContext(defaultDispatcher) {
    actionSettings[key]
}
