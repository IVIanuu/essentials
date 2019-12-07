package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.app.AppInitializers
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Module
import kotlin.reflect.KClass

inline fun <reified T : AccessibilityComponent> Module.bindAccessibilityComponent(
    name: Any? = null
) {
    withBinding<T>(name) { bindAccessibilityComponent() }
}

inline fun <reified T : AccessibilityComponent> BindingContext<T>.bindAccessibilityComponent(): BindingContext<T> {
    intoMap<KClass<out AccessibilityComponent>, AccessibilityComponent>(
        entryKey = T::class,
        mapName = AppInitializers
    )
    return this
}