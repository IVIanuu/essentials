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
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.withContext

typealias getAllActions = suspend () -> List<Action>
@Binding
fun getActions(
    actions: Set<Action>,
    defaultDispatcher: DefaultDispatcher,
): getAllActions = {
    withContext(defaultDispatcher) { actions.toList() }
}

typealias getAction = suspend (String) -> Action
@Binding
fun getAction(
    actions: Set<Action>,
    actionFactories: () -> Set<ActionFactory>,
    defaultDispatcher: DefaultDispatcher,
): getAction = { key ->
    withContext(defaultDispatcher) {
        actions
            .firstOrNull { it.key == key }
            ?: actionFactories()
                .firstOrNull { it.handles(key) }
                ?.createAction(key)
            ?: error("Unsupported action key $key")
    }
}
