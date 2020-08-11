package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given

@Effect
annotation class GivenNotificationWorker {
    companion object {
        @SetElements
        operator fun <T : suspend () -> Unit> invoke(): NotificationWorkers = setOf(given<T>())
    }
}

object NotificationModule {
    @SetElements
    fun notificationWorkers(): NotificationWorkers = emptySet()
}

typealias NotificationWorkers = Set<suspend () -> Unit>
