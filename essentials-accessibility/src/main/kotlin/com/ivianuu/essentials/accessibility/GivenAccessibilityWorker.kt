package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given

@Effect
annotation class GivenAccessibilityWorker {
    companion object {
        @SetElements
        operator fun <T : suspend () -> Unit> invoke(): AccessibilityWorkers = setOf(given<T>())
    }
}

object AccessibilityModule {
    @SetElements
    fun accessibilityWorkers(): AccessibilityWorkers = emptySet()
}

typealias AccessibilityWorkers = Set<suspend () -> Unit>
