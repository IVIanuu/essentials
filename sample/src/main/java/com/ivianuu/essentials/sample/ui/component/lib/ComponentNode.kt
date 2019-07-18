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

data class ComponentNode(
    val component: UiComponent<*>,
    val containerId: Int?,
    var children: MutableList<ComponentNode>?
) {
    var parent: ComponentNode? = null
        internal set
}

class ComponentNodeBuildContext(
    override val componentContext: ComponentContext,
    private val thisNode: ComponentNode
) : BuildContext {
    override fun emit(component: UiComponent<*>, containerId: Int?) {
        // todo check duplicate
        if (thisNode.children == null) thisNode.children = mutableListOf()
        val newNode = ComponentNode(component, containerId, null).also {
            it.parent = thisNode
        }
        component.buildChildren(ComponentNodeBuildContext(componentContext, newNode))
        thisNode.children!!.add(newNode)
    }

}