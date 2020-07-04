package com.ivianuu.essentials.sample

import com.ivianuu.essentials.accessibility.AccessibilityWorker
import com.ivianuu.essentials.accessibility.BindAccessibilityWorker
import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Unscoped

@BindAccessibilityWorker
@Unscoped
class AccessibilityLogger(
    private val logger: Logger
) : AccessibilityWorker {

    override suspend fun run() = runWithCleanup(
        block = {
            logger.d("hello from accessibility")
            awaitCancellation()
        },
        cleanup = {
            logger.d("bye from accessibility")
        }
    )

}
