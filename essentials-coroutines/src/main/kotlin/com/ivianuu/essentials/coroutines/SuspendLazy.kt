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

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart.LAZY
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> CoroutineScope.suspendLazy(
    context: CoroutineContext = EmptyCoroutineContext,
    initializer: suspend CoroutineScope.() -> T
): SuspendLazy<T> = SuspendLazyImpl(this, context, initializer)

interface SuspendLazy<T> {
    suspend operator fun invoke(): T
}

private class SuspendLazyImpl<T>(
    coroutineScope: CoroutineScope,
    context: CoroutineContext,
    initializer: suspend CoroutineScope.() -> T
) : SuspendLazy<T> {
    private val deferred by lazy { coroutineScope.async(context, start = LAZY, block = initializer) }
    override suspend operator fun invoke(): T = deferred.await()
}
