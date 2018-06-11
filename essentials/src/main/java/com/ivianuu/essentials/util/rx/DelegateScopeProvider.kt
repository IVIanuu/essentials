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

import com.uber.autodispose.ScopeProvider

/**
 * A [ScopeProvider] which wraps another [ScopeProvider]
 */
interface DelegateScopeProvider : ScopeProvider {
    val scopeProvider: ScopeProvider
    override fun requestScope() = scopeProvider.requestScope()
}