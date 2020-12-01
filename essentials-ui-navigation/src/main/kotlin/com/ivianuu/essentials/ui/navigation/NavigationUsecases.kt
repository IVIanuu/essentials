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

import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.navigation.NavigationAction.*
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CompletableDeferred

@Suppress("UNCHECKED_CAST")
@FunBinding
suspend fun <K : Key, R> pushKeyForResult(
    dispatchNavigationAction: DispatchAction<NavigationAction>,
    @FunApi key: K,
): R? {
    val result = CompletableDeferred<R?>()
    dispatchNavigationAction(Push(key, result as CompletableDeferred<Any?>))
    return result.await()
}

@FunBinding
fun <R> popTopKeyWithResult(
    dispatchNavigationAction: DispatchAction<NavigationAction>,
    @FunApi result: R?,
) {
    dispatchNavigationAction(PopTop(result))
}
