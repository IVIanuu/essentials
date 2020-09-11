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

package com.ivianuu.essentials.app

import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.Adapter
import com.ivianuu.injekt.given
import com.ivianuu.injekt.keyOf
import kotlinx.coroutines.launch

@Adapter
annotation class GivenAppWorker {
    companion object : Adapter.Impl<suspend () -> Unit> {
        override fun ContextBuilder.configure(
            key: Key<suspend () -> Unit>,
            provider: @Reader () -> suspend () -> Unit
        ) {
            set(keyOf<AppWorkers>()) {
                add(key, elementProvider = provider)
            }
        }
    }
}

object EsAppWorkersGivens {
    @Given
    fun appWorkers(): AppWorkers = emptySet()
}

typealias AppWorkers = Set<suspend () -> Unit>

@Reader
fun runAppWorkers() {
    d { "run workers" }
    given<AppWorkers>()
        .forEach { worker ->
            globalScope.launch {
                worker()
            }
        }
}
