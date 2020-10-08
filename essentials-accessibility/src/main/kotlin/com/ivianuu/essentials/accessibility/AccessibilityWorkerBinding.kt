package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ServiceComponent::class)
annotation class AccessibilityWorkerBinding {
    @Module
    class ModuleImpl<T : suspend () -> Unit> {
        @SetElements
        fun intoSet(instance: T): AccessibilityWorkers = setOf(instance)
    }
}

@SetElements
fun defaultAccessibilityWorkers(): AccessibilityWorkers = emptySet()

typealias AccessibilityWorkers = Set<suspend () -> Unit>
