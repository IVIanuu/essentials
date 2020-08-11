package com.ivianuu.essentials.sample

import com.ivianuu.essentials.accessibility.GivenAccessibilityWorker
import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.d

@GivenAccessibilityWorker
suspend fun logAccessibility() = runWithCleanup(
    block = {
        d { "hello from accessibility" }
        awaitCancellation()
    },
    cleanup = {
        d { "bye from accessibility" }
    }
)
