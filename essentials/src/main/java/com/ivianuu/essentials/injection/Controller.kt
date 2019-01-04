package com.ivianuu.essentials.injection

import com.ivianuu.director.Controller
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.instanceModule

/**
 * Returns a [Component] with convenient configurations
 */
fun <T : Controller> controllerComponent(
    instance: T,
    name: String? = instance.javaClass.simpleName + "Component",
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
) = component(name, createEagerInstances) {
    instance.parentComponentOrNull()?.let { components(it) }
    modules(instanceModule(instance), controllerModule(instance))
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
fun Controller.parentComponent() = parentComponentOrNull() ?: error("No parent found for $this")

const val WORKER = "controller"

/**
 * Returns a [Module] with convenient declarations
 */
fun <T : Controller> controllerModule(
    instance: T,
    name: String? = "ControllerModule"
) = module(name) {
    // controller
    factory(WORKER) { instance as Controller }
}

fun DefinitionContext.controller() = get<Controller>(WORKER)