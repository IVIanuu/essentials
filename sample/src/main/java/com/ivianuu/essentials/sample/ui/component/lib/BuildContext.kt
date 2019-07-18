/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample.ui.component.lib

interface BuildContext {
    val componentContext: ComponentContext
    fun emit(component: UiComponent<*>, containerId: Int? = null) {
    }
}

class ComponentBuildContext(
    override val componentContext: ComponentContext,
    private val thisComponent: UiComponent<*>
) : BuildContext {
    override fun emit(component: UiComponent<*>, containerId: Int?) {
        // todo check duplicate
        if (thisComponent.children == null) thisComponent.children = mutableListOf()
        component.parent = thisComponent
        component.containerId = containerId
        component.buildChildren(ComponentBuildContext(componentContext, component))
        thisComponent.children!!.add(component)
    }

}