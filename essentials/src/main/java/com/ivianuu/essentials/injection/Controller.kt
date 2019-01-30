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

import com.ivianuu.director.Controller
import com.ivianuu.director.application
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentDefinition
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.addInstance
import com.ivianuu.injekt.component
import com.ivianuu.injekt.dependencies
import com.ivianuu.injekt.scopeNames

const val CONTROLLER_SCOPE = "controller_scope"
const val CHILD_CONTROLLER_SCOPE = "child_controller_scope"

/**
 * Returns a [Component] with convenient configurations
 */
fun <T : Controller> controllerComponent(
    instance: T,
    name: String? = instance.javaClass.simpleName + "Component",
    deferCreateEagerInstances: Boolean = false,
    definition: ComponentDefinition? = null
): Component = component(name, deferCreateEagerInstances) {
    scopeNames(CONTROLLER_SCOPE)
    (instance.getParentControllerComponentOrNull()
        ?: instance.getActivityComponentOrNull()
        ?: instance.getApplicationComponentOrNull())?.let { dependencies(it) }
    addInstance(instance)
    definition?.invoke(this)
}

/**
 * Returns a [Component] with convenient configurations
 */
fun <T : Controller> childControllerComponent(
    instance: T,
    name: String? = instance.javaClass.simpleName + "Component",
    definition: ComponentDefinition? = null
): Component = component(name) {
    scopeNames(CHILD_CONTROLLER_SCOPE)
    (instance.getParentControllerComponentOrNull()
        ?: instance.getActivityComponentOrNull()
        ?: instance.getApplicationComponentOrNull())?.let { dependencies(it) }
    addInstance(instance)
    definition?.invoke(this)
}

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
    (application as? InjektTrait)?.component

/**
 * Returns the [Component] of the application or throws
 */
fun Controller.getApplicationComponent(): Component =
    getApplicationComponentOrNull() ?: error("No application component found for $this")