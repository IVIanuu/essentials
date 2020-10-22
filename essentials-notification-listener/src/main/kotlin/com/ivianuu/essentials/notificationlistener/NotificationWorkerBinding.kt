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

import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ServiceComponent::class)
annotation class NotificationWorkerBinding {
    companion object {
        @SetElements
        fun <T : suspend () -> Unit> workerIntoSet(instance: T): NotificationWorkers = setOf(instance)
    }
}

typealias NotificationWorkers = Set<suspend () -> Unit>

@SetElements
fun defaultNotificationWorkers(): NotificationWorkers = emptySet()
