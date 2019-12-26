/*
 * Copyright 2019 Manuel Wrage
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

import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.coroutines.cancelBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap

val Scope.coroutineScope: CoroutineScope
    get() = CoroutineScopeByScope.getOrPut(this) {
        ScopedCoroutineScope(this)
            .also {
                onClose {
                    CoroutineScopeByScope -= this
                }
            }
    }

private class ScopedCoroutineScope(scope: Scope) : CoroutineScope {
    private val job = Job().cancelBy(scope)
    override val coroutineContext = job + Dispatchers.Main
}

private val CoroutineScopeByScope = ConcurrentHashMap<Scope, CoroutineScope>()

