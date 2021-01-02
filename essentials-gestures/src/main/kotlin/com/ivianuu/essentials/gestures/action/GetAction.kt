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
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.withContext

@GivenFun suspend fun getAllActions(
    @Given actions: Map<String, () -> Action>,
    @Given defaultDispatcher: DefaultDispatcher,
): List<Action> = withContext(defaultDispatcher) {
    actions.values
        .map { it() }
}

@GivenFun suspend fun getAction(
    key: String,
    @Given actions: Map<String, () -> Action>,
    @Given actionFactories: () -> Set<ActionFactory>,
    @Given defaultDispatcher: DefaultDispatcher
): Action = withContext(defaultDispatcher) {
    actions[key]
        ?.invoke()
        ?: actionFactories()
            .firstOrNull { it.handles(key) }
            ?.createAction(key)
        ?: error("Unsupported action key $key")
}

@GivenFun suspend fun getActionExecutor(
    key: String,
    @Given actionsExecutors: Map<String, ActionExecutor>,
    @Given actionFactories: () -> Set<ActionFactory>,
    @Given defaultDispatcher: DefaultDispatcher
): ActionExecutor = withContext(defaultDispatcher) {
    actionsExecutors[key]
        ?: actionFactories()
            .firstOrNull { it.handles(key) }
            ?.createExecutor(key)
        ?: error("Unsupported action key $key")
}

@GivenFun suspend fun getActionSettingsKey(
    id: String,
    @Given actionSettings: Map<String, Key>,
    @Given defaultDispatcher: DefaultDispatcher
): Key? = withContext(defaultDispatcher) {
    actionSettings[id]
}
