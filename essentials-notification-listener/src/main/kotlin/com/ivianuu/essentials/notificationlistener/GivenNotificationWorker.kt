package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.Adapter
import com.ivianuu.injekt.keyOf

@Adapter
annotation class GivenNotificationWorker {
    companion object : Adapter.Impl<suspend () -> Unit> {
        override fun ContextBuilder.configure(
            key: Key<suspend () -> Unit>,
            provider: @Reader () -> suspend () -> Unit
        ) {
            set(keyOf<NotificationWorkers>()) {
                add(key, elementProvider = provider)
            }
        }
    }
}

object EsNotificationGivens {
    @Given
    fun notificationWorkers(): NotificationWorkers = emptySet()
}

typealias NotificationWorkers = Set<suspend () -> Unit>
