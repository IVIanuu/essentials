package com.ivianuu.essentials.injection

import com.ivianuu.director.Controller
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentDefinition
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.common.instanceModule
import com.ivianuu.injekt.component

/**
 * Returns a [Component] with convenient configurations
 */
fun <T : Controller> controllerComponent(
    instance: T,
    name: String? = instance.javaClass.simpleName + "Component",
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
): Component = component(name, createEagerInstances) {
    instance.parentComponentOrNull()?.let { components(it) }
    modules(instanceModule(instance))
    definition?.invoke(this)
}

/**
 * Returns the parent [Component] if available or null
 */
fun Controller.parentComponentOrNull(): Component? {
    var parentController = parentController

    while (parentController != null) {
        if (parentController is InjektTrait) {
            return parentController.component
        }
        parentController = parentController.parentController
    }

    (activity as? InjektTrait)?.component?.let { return it }
    (activity.applicationContext as? InjektTrait)?.component?.let { return it }

    return null
}

/**
 * Returns the parent [Component] or throws
 */
fun Controller.parentComponent(): Component = parentComponentOrNull() ?: error("No parent found for $this")