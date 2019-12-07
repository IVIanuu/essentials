package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Name
import com.ivianuu.injekt.module

@Name
annotation class AccessibilityComponents {
    companion object
}


inline fun <reified T : AccessibilityComponent> Module.bindAccessibilityComponent(
    name: Any? = null
) {
    withBinding<T>(name) { bindAccessibilityComponent() }
}

fun <T : AccessibilityComponent> BindingContext<T>.bindAccessibilityComponent(): BindingContext<T> {
    intoSet<AccessibilityComponent>(setName = AccessibilityComponents)
    return this
}


internal val accessibilityComponentsModule = module {
    set<AccessibilityComponent>(setName = AccessibilityComponents)
}