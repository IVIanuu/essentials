package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.Adapter
import com.ivianuu.injekt.keyOf

@Adapter
annotation class GivenAccessibilityWorker {
    companion object : Adapter.Impl<suspend () -> Unit> {
        override fun ContextBuilder.configure(
            key: Key<suspend () -> Unit>,
            provider: @Reader () -> suspend () -> Unit
        ) {
            set(keyOf<AccessibilityWorkers>()) {
                add(key, elementProvider = provider)
            }
        }
    }
}

object AccessibilityGivens {
    @Given
    fun accessibilityWorkers(): AccessibilityWorkers = emptySet()
}

typealias AccessibilityWorkers = Set<suspend () -> Unit>
