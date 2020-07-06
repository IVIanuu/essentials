package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.set

/**
 * Runs while a notification service is alive
 */
@BindingEffect(ServiceComponent::class)
annotation class NotificationWorker {
    companion object {
        @Module
        operator fun <T : suspend () -> Unit> invoke() {
            set<@NotificationWorkers Set<suspend () -> Unit>, suspend () -> Unit> {
                add<T>()
            }
        }
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class NotificationWorkers

@Module
fun EsNotificationWorkerModule() {
    installIn<ServiceComponent>()
    set<@NotificationWorkers Set<suspend () -> Unit>, suspend () -> Unit>()
}
