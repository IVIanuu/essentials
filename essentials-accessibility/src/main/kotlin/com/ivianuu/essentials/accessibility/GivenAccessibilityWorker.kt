package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.GivenSetElements
import com.ivianuu.injekt.given

@Effect
annotation class GivenAccessibilityWorker {
    companion object {
        @GivenSetElements
        operator fun <T : suspend () -> Unit> invoke(): AccessibilityWorkers = setOf(given<T>())
    }
}

object AccessibilityModule {
    @GivenSetElements
    fun accessibilityWorkers(): AccessibilityWorkers = emptySet()
}

typealias AccessibilityWorkers = Set<suspend () -> Unit>
