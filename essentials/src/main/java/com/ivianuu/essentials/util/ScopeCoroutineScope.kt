/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.util

import com.ivianuu.essentials.util.ext.coroutinesMain
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.coroutines.cancelBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

fun ScopeCoroutineScope(
    scope: Scope,
    coroutineContext: CoroutineContext? = null
): CoroutineScope = if (coroutineContext != null) {
    CoroutineScope(coroutineContext + Job().cancelBy(scope))
} else {
    CoroutineScope(Job().cancelBy(scope))
}

fun MainScopeCoroutineScope(
    scope: Scope,
    coroutineContext: CoroutineContext? = null
): CoroutineScope = if (coroutineContext != null) {
    ScopeCoroutineScope(scope, coroutineContext + coroutinesMain)
} else {
    ScopeCoroutineScope(scope, coroutinesMain)
}

fun Scope.asCoroutineScope(coroutineContext: CoroutineContext? = null): CoroutineScope =
    ScopeCoroutineScope(this, coroutineContext)

fun Scope.asMainCoroutineScope(coroutineContext: CoroutineContext? = null): CoroutineScope =
    MainScopeCoroutineScope(this, coroutineContext)