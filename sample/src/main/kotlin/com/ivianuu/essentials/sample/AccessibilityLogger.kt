package com.ivianuu.essentials.sample

import com.ivianuu.essentials.accessibility.AccessibilityWorker
import com.ivianuu.essentials.coroutines.awaitCancellation
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Reader

@AccessibilityWorker
@Reader
suspend fun logAccessibility() = runWithCleanup(
    block = {
        d { "hello from accessibility" }
        awaitCancellation()
    },
    cleanup = {
        d { "bye from accessibility" }
    }
)
