package com.ivianuu.essentials.sample

import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.notificationlistener.NotificationWorker
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped

@NotificationWorker
@Reader
suspend fun logNotifications() = runWithCleanup(
    block = {
        d("hello from notifications")
        awaitCancellation()
    },
    cleanup = {
        d("bye from notifications")
    }
)
