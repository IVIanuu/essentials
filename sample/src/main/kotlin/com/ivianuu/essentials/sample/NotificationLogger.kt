package com.ivianuu.essentials.sample

import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.notificationlistener.BindNotificationWorker
import com.ivianuu.essentials.notificationlistener.NotificationWorker
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Unscoped

@BindNotificationWorker
@Unscoped
class NotificationLogger(
    private val logger: Logger
) : NotificationWorker {

    override suspend fun run() = runWithCleanup(
        block = {
            logger.d("hello from notifications")
            awaitCancellation()
        },
        cleanup = {
            logger.d("bye from notifications")
        }
    )

}
