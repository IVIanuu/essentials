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

import com.ivianuu.essentials.ui.coroutines.UiScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.SetElements
import kotlinx.coroutines.launch

@Effect
annotation class UiWorkerBinding {
    companion object {
        @SetElements
        fun <T : suspend () -> Unit> workerIntoSet(instance: @ForEffect T): UiWorkers =
            setOf(instance)
    }
}

typealias UiWorkers = Set<suspend () -> Unit>

@SetElements
fun defaultUiWorkers(): UiWorkers = emptySet()

@FunBinding
fun runUiWorkers(
    logger: Logger,
    uiScope: UiScope,
    workers: UiWorkers,
) {
    logger.d { "run ui workers" }
    workers
        .forEach { worker ->
            uiScope.launch {
                worker()
            }
        }
}
