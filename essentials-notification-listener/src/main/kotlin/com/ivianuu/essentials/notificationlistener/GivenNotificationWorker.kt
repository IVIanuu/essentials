package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.GivenSetElements
import com.ivianuu.injekt.given

@Effect
annotation class GivenNotificationWorker {
    companion object {
        @GivenSetElements
        operator fun <T : suspend () -> Unit> invoke(): NotificationWorkers = setOf(given<T>())
    }
}

object NotificationModule {
    @GivenSetElements
    fun notificationWorkers(): NotificationWorkers = emptySet()
}

typealias NotificationWorkers = Set<suspend () -> Unit>
