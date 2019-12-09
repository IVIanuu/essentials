package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Name

@Name
annotation class AccessibilityComponents {
    companion object
}


inline fun <reified T : AccessibilityComponent> ModuleBuilder.bindAccessibilityComponent(
    name: Any? = null
) {
    withBinding<T>(name) { bindAccessibilityComponent() }
}

fun <T : AccessibilityComponent> BindingContext<T>.bindAccessibilityComponent(): BindingContext<T> {
    intoSet<AccessibilityComponent>(setName = AccessibilityComponents)
    return this
}


internal val AccessibilityComponentsModule = Module {
    set<AccessibilityComponent>(setName = AccessibilityComponents)
}