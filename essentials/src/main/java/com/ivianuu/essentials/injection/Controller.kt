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
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.Scope
import com.ivianuu.injekt.ScopeAnnotation
import com.ivianuu.injekt.bindAlias
import com.ivianuu.injekt.bindName
import com.ivianuu.injekt.bindType
import com.ivianuu.injekt.component
import com.ivianuu.injekt.constant.constant
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.module

private inline fun androidComponent(
    scope: Scope? = null,
    modules: Iterable<Module>,
    dependencies: Iterable<Component>,
    module: () -> Module,
    dependency: () -> Component?
): Component {
    val allModules = modules + listOf(module())
    val allDependencies = dependencies +
            (dependency()?.let { listOf(it) } ?: emptyList())
    return component(scope, allModules, allDependencies)
}

@ScopeAnnotation(ControllerScope.Companion::class)
annotation class ControllerScope {
    companion object : Scope
}

@ScopeAnnotation(ChildControllerScope.Companion::class)
annotation class ChildControllerScope {
    companion object : Scope
}

@Name(ForController.Companion::class)
annotation class ForController {
    companion object : Qualifier
}

@Name(ForChildController.Companion::class)
annotation class ForChildController {
    companion object : Qualifier
}

fun <T : Controller> T.controllerComponent(block: ComponentBuilder.() -> Unit): Component =
    component {
        scope = ControllerScope
        getClosestComponentOrNull()?.let { dependencies(it) }
        modules(controllerModule())
        block()
    }

fun <T : Controller> T.controllerComponent(
    scope: Scope? = ControllerScope,
    modules: Iterable<Module> = emptyList(),
    dependencies: Iterable<Component> = emptyList()
): Component = androidComponent(
    scope, modules, dependencies,
    { controllerModule() },
    { getClosestComponentOrNull() }
)

fun <T : Controller> T.childControllerComponent(block: ComponentBuilder.() -> Unit): Component =
    component {
        scope = ChildControllerScope
        getClosestComponentOrNull()?.let { dependencies(it) }
        modules(childControllerModule())
        block()
    }

fun <T : Controller> T.childControllerComponent(
    scope: Scope? = ChildControllerScope,
    modules: Iterable<Module> = emptyList(),
    dependencies: Iterable<Component> = emptyList()
): Component = androidComponent(
    scope, modules, dependencies,
    { childControllerModule() },
    { getClosestComponentOrNull() }
)

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

private fun <T : Controller> T.internalControllerModule(name: Qualifier) = module {
    constant(this@internalControllerModule, override = true).apply {
        bindType<Controller>()
        bindAlias<Controller>(name)
        bindType<LifecycleOwner>()
        bindAlias<LifecycleOwner>(name)
        bindType<ViewModelStoreOwner>()
        bindAlias<ViewModelStoreOwner>(name)
        bindType<SavedStateRegistryOwner>()
        bindAlias<SavedStateRegistryOwner>(name)
    }

    factory(override = true) { resources } bindName name
    factory(override = true) { lifecycle } bindName name
    factory(override = true) { viewModelStore } bindName name
    factory(override = true) { savedStateRegistry } bindName name
    factory(override = true) { childRouterManager } bindName name
}