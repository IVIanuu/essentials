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

package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Qualifier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Qualifier annotation class NotificationWorkerBinding

@Given @GivenSetElement
fun <T : @NotificationWorkerBinding NotificationWorker> workerIntoSet(
    @Given instance: T): NotificationWorker = instance

typealias NotificationWorker = suspend () -> Unit

@GivenFun suspend fun runNotificationWorkers(@Given workers: Set<NotificationWorker>) {
    coroutineScope {
        workers.forEach { worker ->
            launch { worker() }
        }
    }
}
