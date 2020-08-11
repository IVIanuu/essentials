package com.ivianuu.essentials.sample

import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.notificationlistener.GivenNotificationWorker
import com.ivianuu.essentials.util.d

@GivenNotificationWorker
suspend fun logNotifications() = runWithCleanup(
    block = {
        d { "hello from notifications" }
        awaitCancellation()
    },
    cleanup = {
        d { "bye from notifications" }
    }
)
