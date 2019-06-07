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

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.ivianuu.director.Controller
import com.ivianuu.director.resources
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Name
import com.ivianuu.injekt.Scope
import com.ivianuu.injekt.android.ChildViewScope
import com.ivianuu.injekt.bindAlias
import com.ivianuu.injekt.bindName
import com.ivianuu.injekt.bindType
import com.ivianuu.injekt.component
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.instance
import com.ivianuu.injekt.module
import com.ivianuu.injekt.scopes

@Scope
annotation class ControllerScope

@Scope
annotation class ChildControllerScope

@Name(ForController.Companion::class)
annotation class ForController {
    companion object
}

@Name(ForChildController.Companion::class)
annotation class ForChildController {
    companion object
}

fun <T : Controller> T.controllerComponent(block: (ComponentBuilder.() -> Unit)? = null): Component =
    component {
        scopes<ControllerScope>()
        getClosestComponentOrNull()?.let { dependencies(it) }
        modules(controllerModule())
        block?.invoke(this)
    }

fun <T : Controller> T.childControllerComponent(block: (ComponentBuilder.() -> Unit)? = null): Component =
    component {
        scopes<ChildViewScope>()
        getClosestComponentOrNull()?.let { dependencies(it) }
        modules(childControllerModule())
        block?.invoke(this)
    }

fun Controller.getClosestComponentOrNull(): Component? {
    return getParentControllerComponentOrNull()
        ?: getActivityComponentOrNull()
        ?: getApplicationComponentOrNull()
}

fun Controller.getClosestComponent(): Component =
    getClosestComponentOrNull() ?: error("No close component found for $this")

fun Controller.getParentControllerComponentOrNull(): Component? =
    (parentController as? InjektTrait)?.component

fun Controller.getParentControllerComponent(): Component =
    getParentControllerComponentOrNull() ?: error("No parent controller component found for $this")

fun Controller.getActivityComponentOrNull(): Component? =
    (activity as? InjektTrait)?.component

fun Controller.getActivityComponent(): Component =
    getActivityComponentOrNull() ?: error("No activity component found for $this")

fun Controller.getApplicationComponentOrNull(): Component? =
    (activity.application as? InjektTrait)?.component

fun Controller.getApplicationComponent(): Component =
    getApplicationComponentOrNull() ?: error("No application component found for $this")

fun <T : Controller> T.controllerModule(): Module = module {
    include(internalControllerModule(ForController))
}

fun <T : Controller> T.childControllerModule(): Module = module {
    include(internalControllerModule(ForChildController))
}

private fun <T : Controller> T.internalControllerModule(name: Any) = module {
    instance(this@internalControllerModule, override = true).apply {
        bindType<Controller>()
        bindAlias<Controller>(name)
        bindType<LifecycleOwner>()
        bindAlias<LifecycleOwner>(name)
        bindType<ViewModelStoreOwner>()
        bindAlias<ViewModelStoreOwner>(name)
        bindType<SavedStateRegistryOwner>()
        bindAlias<SavedStateRegistryOwner>(name)
    }

    factory<Context>(override = true) { activity } bindName name
    factory(override = true) { resources } bindName name
    factory(override = true) { lifecycle } bindName name
    factory(override = true) { viewModelStore } bindName name
    factory(override = true) { savedStateRegistry } bindName name
    factory(override = true) { childRouterManager } bindName name
}