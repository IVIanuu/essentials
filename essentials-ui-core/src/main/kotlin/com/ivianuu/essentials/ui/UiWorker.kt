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

package com.ivianuu.essentials.ui

import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.essentials.ui.coroutines.UiScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import kotlinx.coroutines.launch

@Qualifier annotation class UiWorkerBinding
@Macro @GivenSetElement
fun <T : @UiWorkerBinding UiWorker> uiWorkerBinding(@Given instance: T): UiWorker = instance

typealias UiWorker = suspend () -> Unit

@GivenFun
fun runUiWorkers(
    @Given defaultDispatcher: DefaultDispatcher,
    @Given logger: Logger,
    @Given uiScope: UiScope,
    @Given workers: Set<UiWorker>,
) {
    logger.d { "run ui workers" }
    workers
        .forEach { worker ->
            uiScope.launch(defaultDispatcher) {
                worker()
            }
        }
}
