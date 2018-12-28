package com.ivianuu.essentials.sample.injekt

import com.ivianuu.director.Controller
import com.ivianuu.director.application
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentHolder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.component

fun Controller.controllerComponent(
    modules: List<Module> = emptyList(),
    dependencies: List<Component> = emptyList()
): Component {
    val deps = mutableSetOf<Component>()

    // application
    (application as? ComponentHolder)?.component?.let { deps.add(it) }

    // activity
    (activity as? ComponentHolder)?.component?.let { deps.add(it) }

    // parent
    (activity as? ComponentHolder)?.component?.let { deps.add(it) }

    deps.addAll(dependencies)

    return component(
        modules = modules,
        dependencies = deps
    )
}
