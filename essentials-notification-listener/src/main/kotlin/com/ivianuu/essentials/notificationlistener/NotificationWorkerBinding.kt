package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.BindingModule
import com.ivianuu.injekt.merge.MergeInto

@BindingModule(ServiceComponent::class)
annotation class NotificationWorkerBinding {
    @Module
    class ModuleImpl<T : suspend () -> Unit> {
        @SetElements
        operator fun invoke(instance: T): NotificationWorkers = setOf(instance)
    }
}

@MergeInto(ServiceComponent::class)
@Module
object EsNotificationModule {
    @SetElements
    fun notificationWorkers(): NotificationWorkers = emptySet()
}

typealias NotificationWorkers = Set<suspend () -> Unit>
