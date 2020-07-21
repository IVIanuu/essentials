package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given

@Effect
annotation class AccessibilityWorker {
    companion object {
        @SetElements(ApplicationComponent::class)
        operator fun <T : suspend () -> Unit> invoke(): AccessibilityWorkers = setOf(given<T>())
    }
}

@Distinct
typealias AccessibilityWorkers = Set<suspend () -> Unit>
