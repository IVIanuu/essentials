package com.ivianuu.essentials.sample

import com.ivianuu.essentials.accessibility.AccessibilityWorkerBinding
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.awaitCancellation

@AccessibilityWorkerBinding
@FunBinding
suspend fun logAccessibility(logger: Logger): Unit = runWithCleanup(
    block = {
        logger.d("hello from accessibility")
        awaitCancellation()
    },
    cleanup = {
        logger.d("bye from accessibility")
    }
)
