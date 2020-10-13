package com.ivianuu.essentials.sample

import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.notificationlistener.NotificationWorkerBinding
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.awaitCancellation

@NotificationWorkerBinding
@FunBinding
suspend fun logNotifications(logger: Logger): Unit = runWithCleanup(
    block = {
        logger.d("hello from notifications")
        awaitCancellation()
    },
    cleanup = {
        logger.d("bye from notifications")
    }
)
