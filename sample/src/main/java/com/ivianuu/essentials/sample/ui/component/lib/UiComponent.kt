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

import android.view.View
import android.view.ViewGroup
import com.github.ajalt.timberkt.d

abstract class UiComponent<V : View> {

    abstract val id: Any?
    abstract val viewId: Int
    open val viewType: Int get() = viewId

    var parent: UiComponent<*>? = null
        set
    var children: MutableList<UiComponent<*>>? = null

    var containerId: Int? = null

    private val state = mutableListOf<Any?>() // todo lazy init

    internal fun addOrUpdate(container: ViewGroup) {
        d { "add or update ${javaClass.simpleName}" }
        var view: V? = container.findViewById<V>(viewId)

        if (view == null) {
            view = createView(container)
            container.addView(view)
            bind(view)
        } else {
            unbind(view)
            bind(view)
        }
    }

    internal fun removeIfPossible(container: ViewGroup) {
        val view: V? = container.findViewById<V>(viewId)
        d { "remove if possible ${javaClass.simpleName} $view" }
        if (view != null) {
            unbind(view)
            container.removeView(view)
        }
    }

    internal fun buildChildren(buildContext: BuildContext) {
        with(buildContext) { children() }
    }

    internal fun _layoutChildren(
        view: V,
        oldChildren: List<UiComponent<*>>?
    ) {
        layoutChildren(view, children, oldChildren)
    }

    protected fun state(vararg state: Any?) {
        this.state.addAll(state)
    }

    open fun bind(view: V) {

    }

    open fun unbind(view: V) {
    }

    abstract fun createView(container: ViewGroup): V

    protected open fun BuildContext.children() {
    }

    protected open fun layoutChildren(
        view: V,
        newChildren: List<UiComponent<*>>?,
        oldChildren: List<UiComponent<*>>?
    ) {
        val processedNodes = mutableListOf<UiComponent<*>>()

        newChildren?.forEach { newChildrenNode ->
            processedNodes.add(newChildrenNode)
            val oldChildrenNode = oldChildren?.firstOrNull {
                it.id == newChildrenNode.id
            }
            if (oldChildrenNode != null) processedNodes.add(oldChildrenNode)
            layout(view, newChildrenNode, oldChildrenNode)
        }

        oldChildren?.forEach { oldChildrenNode ->
            if (!processedNodes.contains(oldChildrenNode)) {
                layout(view, null, oldChildrenNode)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UiComponent<*>) return false

        if (id != other.id) return false
        if (viewId != other.viewId) return false
        if (viewType != other.viewType) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + viewId
        result = 31 * result + viewType
        result = 31 * result + state.hashCode()
        return result
    }

    private fun layout(
        view: V,
        newNode: UiComponent<*>?,
        oldNode: UiComponent<*>?
    ) {
        d { "layout node new $newNode old $oldNode in $view" }
        fun UiComponent<*>.containerOrThis() =
            containerId?.let { view.findViewById<ViewGroup>(it) } ?: view as ViewGroup

        if (newNode != null && oldNode == null) {
            newNode.addOrUpdate(newNode.containerOrThis())
        } else if (newNode != null && oldNode != null) {
            if (newNode != oldNode) {
                if (newNode.viewType == oldNode.viewType) {
                    newNode.addOrUpdate(newNode.containerOrThis())
                } else {
                    newNode.addOrUpdate(newNode.containerOrThis())
                    oldNode.removeIfPossible(newNode.containerOrThis())
                }
            }
        } else if (newNode == null && oldNode != null) {
            oldNode.removeIfPossible(oldNode.containerOrThis())
        }

        (newNode as? UiComponent<View>)
            ?._layoutChildren(containerOrThis().findViewById(newNode.viewId), oldNode?.children)
    }

    override fun toString(): String =
        "Component(id=$id, viewId=$viewId, parent=${parent?.javaClass?.name}, children=$children, containerId=$containerId, state=$state)"

}