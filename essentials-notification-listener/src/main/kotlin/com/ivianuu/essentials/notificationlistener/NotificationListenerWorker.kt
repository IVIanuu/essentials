package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.BindingEffectFunction
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import kotlin.reflect.KClass

/**
 * Runs while a notification service is alive
 */
interface NotificationWorker {
    suspend fun run()
}

@BindingEffect(ServiceComponent::class)
annotation class BindNotificationWorker

@BindingEffectFunction(BindNotificationWorker::class)
@Module
inline fun <reified T : NotificationWorker> notificationWorker() {
    map<KClass<*>, NotificationWorker> {
        put<T>(T::class)
    }
}

@Module
fun esNotificationRunnableModule() {
    installIn<ServiceComponent>()
    map<KClass<*>, NotificationWorker>()
}