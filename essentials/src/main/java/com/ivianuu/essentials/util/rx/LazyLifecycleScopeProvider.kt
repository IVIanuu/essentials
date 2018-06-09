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

import com.ivianuu.autodispose.LifecycleScopeProvider

/**
 * A [LifecycleScopeProvider] which wraps another [lifecycleScopeProvider]
 */
interface LazyLifecycleScopeProvider<T> : LifecycleScopeProvider<T> {
    val lifecycleScopeProvider: LifecycleScopeProvider<T>
    override fun correspondingEvents() = lifecycleScopeProvider.correspondingEvents()
    override fun lifecycle() = lifecycleScopeProvider.lifecycle()
    override fun peekLifecycle() = lifecycleScopeProvider.peekLifecycle()
}

/**
 * A helper class to implement [LifecycleScopeProvider] by a provided [LifecycleScopeProvider]
 * without boilerplate
 */
class LazyLifecycleScopeProviderImpl<T> : LazyLifecycleScopeProvider<T> {
    override lateinit var lifecycleScopeProvider: LifecycleScopeProvider<T>
}