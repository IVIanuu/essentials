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

package com.ivianuu.essentials.ui.base

import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import java.io.Closeable

/**
 * Base view model
 */
abstract class ViewModel : Closeable {

    private val _scope = MutableScope()
    val scope: Scope get() = _scope

    override fun close() {
        _scope.close()
    }
}
