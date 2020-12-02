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

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.store.Initial
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.CompletableDeferred

data class NavigationState(val backStack: List<Key> = emptyList()) {
    companion object {
        @Binding
        fun initial(homeKey: HomeKey?): @Initial NavigationState = NavigationState(
            listOfNotNull(homeKey)
        )
    }
}

sealed class NavigationAction {
    data class Push(
        val key: Key,
        val deferredResult: CompletableDeferred<Any?>? = null,
    ) : NavigationAction()

    data class ReplaceTop(
        val key: Key,
        val deferredResult: CompletableDeferred<Any?>? = null,
    ) : NavigationAction()

    data class Pop(
        val key: Key,
        val result: Any? = null,
    ) : NavigationAction()

    data class PopTop(val result: Any? = null) : NavigationAction()
}