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
import com.ivianuu.director.activity
import com.ivianuu.director.application
import com.ivianuu.director.parentController
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.addConstant

object PerController : StringScope("PerController")
object PerChildController : StringScope("PerChildController")

object ForController : StringQualifier("ForController")
object ForChildController : StringQualifier("ForChildController")

/**
 * Returns a [Component] with convenient configurations
 */
inline fun <reified T : Controller> T.controllerComponent(
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition = {}
): Component = component(createEagerInstances) {
    scopes(PerController)
    (getParentControllerComponentOrNull()
        ?: getActivityComponentOrNull()
        ?: getApplicationComponentOrNull())?.let { dependencies(it) }
    addConstant(this@controllerComponent)
    definition.invoke(this)
}

/**
 * Returns a [Component] with convenient configurations
 */
inline fun <reified T : Controller> T.childControllerComponent(
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition = {}
): Component = component(createEagerInstances) {
    scopes(PerChildController)
    (getParentControllerComponentOrNull()
        ?: getActivityComponentOrNull()
        ?: getApplicationComponentOrNull())?.let { dependencies(it) }
    addConstant(this@childControllerComponent)
    definition.invoke(this)
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