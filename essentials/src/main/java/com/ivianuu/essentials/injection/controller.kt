/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.injection

import com.ivianuu.director.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.constant.constant

private inline fun androidComponent(
    modules: Iterable<Module>,
    dependencies: Iterable<Component>,
    module: () -> Module,
    dependency: () -> Component?
): Component {
    val allModules = modules + listOf(module())
    val allDependencies = dependencies +
            (dependency()?.let { listOf(it) } ?: emptyList())
    return component(allModules, allDependencies)
}


/**
 * Controller name
 */
object ForController

/**
 * Child controller name
 */
object ForChildController

/**
 * Returns a [Component] with convenient configurations
 */
fun <T : Controller> T.controllerComponent(
    modules: Iterable<Module> = emptyList(),
    dependencies: Iterable<Component> = emptyList()
): Component = androidComponent(
    modules, dependencies,
    { controllerModule() },
    { getClosestComponentOrNull() }
)

/**
 * Returns a [Component] with convenient configurations
 */
fun <T : Controller> T.childControllerComponent(
    modules: Iterable<Module> = emptyList(),
    dependencies: Iterable<Component> = emptyList()
): Component = androidComponent(
    modules, dependencies,
    { childControllerModule() },
    { getClosestComponentOrNull() }
)

/**
 * Returns the closest [Component] or null
 */
fun Controller.getClosestComponentOrNull(): Component? {
    return getParentControllerComponentOrNull()
        ?: getActivityComponentOrNull()
        ?: getApplicationComponentOrNull()
}

/**
 * Returns the closest [Component]
 */
fun Controller.getClosestComponent(): Component =
    getClosestComponentOrNull() ?: error("No close component found for $this")

/**
 * Returns the [Component] of the parent controller or null
 */
fun Controller.getParentControllerComponentOrNull(): Component? =
    (parentController as? InjektTrait)?.component

/**
 * Returns the [Component] of the parent controller or throws
 */
fun Controller.getParentControllerComponent(): Component =
    getParentControllerComponentOrNull() ?: error("No parent controller component found for $this")

/**
 * Returns the [Component] of the activity or null
 */
fun Controller.getActivityComponentOrNull(): Component? =
    (activity as? InjektTrait)?.component

/**
 * Returns the [Component] of the activity or throws
 */
fun Controller.getActivityComponent(): Component =
    getActivityComponentOrNull() ?: error("No activity component found for $this")

/**
 * Returns the [Component] of the application or null
 */
fun Controller.getApplicationComponentOrNull(): Component? =
    (activity?.application as? InjektTrait)?.component

/**
 * Returns the [Component] of the application or throws
 */
fun Controller.getApplicationComponent(): Component =
    getApplicationComponentOrNull() ?: error("No application component found for $this")


/**
 * Returns a [Module] with convenient bindings
 */
fun <T : Controller> T.controllerModule(): Module = module {
    include(internalControllerModule(ForController))
}

/**
 * Returns a [Module] with convenient bindings
 */
fun <T : Controller> T.childControllerModule(): Module = module {
    include(internalControllerModule(ForChildController))
}

private fun <T : Controller> T.internalControllerModule(qualifier: Any) = module {
    constant(this@internalControllerModule).apply {
        bindType<Controller>()
        bindAlias<Controller>(qualifier)
    }

    factory { context } bindName qualifier
    factory { resources } bindName qualifier
}
