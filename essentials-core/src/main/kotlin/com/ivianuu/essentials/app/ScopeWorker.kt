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

package com.ivianuu.essentials.app

import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.GivenScope
import kotlinx.coroutines.launch

typealias ScopeWorker<S> = suspend () -> Unit

typealias ScopeWorkerRunner<S> = () -> Unit

@Given
fun <@ForTypeKey S : GivenScope> scopeWorkerRunner(
    @Given logger: Logger,
    @Given scope: ScopeCoroutineScope<S>,
    @Given workers: Set<() -> ScopeWorker<S>> = emptySet()
): ScopeWorkerRunner<S> = {
    logger.d { "${typeKeyOf<S>()} run scope workers" }
    workers
        .forEach { worker ->
            scope.launch {
                worker()()
            }
        }
}
