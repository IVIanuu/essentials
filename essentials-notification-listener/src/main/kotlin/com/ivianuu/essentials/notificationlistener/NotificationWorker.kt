package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given

@Effect
annotation class NotificationWorker {
    companion object {
        @SetElements(ApplicationComponent::class)
        operator fun <T : suspend () -> Unit> invoke(): NotificationWorkers = setOf(given<T>())
    }
}

@Distinct
typealias NotificationWorkers = Set<suspend () -> Unit>
