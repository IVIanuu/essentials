package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ServiceComponent::class)
annotation class NotificationWorkerBinding {
    @Module
    class ModuleImpl<T : suspend () -> Unit> {
        @SetElements
        operator fun invoke(instance: T): NotificationWorkers = setOf(instance)
    }
}

typealias NotificationWorkers = Set<suspend () -> Unit>

@SetElements
fun defaultNotificationWorkers(): NotificationWorkers = emptySet()
