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

package com.ivianuu.essentials.util.rx

import com.ivianuu.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * A [LifecycleScopeProvider] which wraps another [lifecycleScopeProvider]
 */
interface DelegateLifecycleScopeProvider<T> : LifecycleScopeProvider<T> {
    val lifecycleScopeProvider: LifecycleScopeProvider<T>
    override fun correspondingEvents(): Function<T, T> =
        lifecycleScopeProvider.correspondingEvents()

    override fun lifecycle(): Observable<T> = lifecycleScopeProvider.lifecycle()
    override fun peekLifecycle() = lifecycleScopeProvider.peekLifecycle()
}