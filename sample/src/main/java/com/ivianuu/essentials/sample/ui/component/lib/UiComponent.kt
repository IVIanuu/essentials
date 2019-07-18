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
    open val viewType: Int
        get() = viewId + (children?.map { it.viewId }?.sum() ?: 0)

    var parent: UiComponent<*>? = null
    var children: MutableList<UiComponent<*>>? = null

    var containerId: Int? = null

    private val state = mutableListOf<Any?>() // todo lazy init

    fun addIfNeeded(container: ViewGroup) {
        d { "add if needed ${javaClass.simpleName}" }
        var view = findViewIn(container)
        if (view == null) {
            view = createView(container)
            container.addView(view)
        }
    }

    fun removeIfPossible(container: ViewGroup) {
        val view = findViewIn(container)
        d { "remove if possible ${javaClass.simpleName} $view" }
        if (view != null) {
            container.removeView(view)
        }
    }

    fun buildChildren(buildContext: BuildContext) {
        d { "build children ${javaClass.simpleName}" }
        with(buildContext) { children() }
    }

    open fun layout(
        container: ViewGroup,
        old: UiComponent<*>?
    ) {
        d { "layout ${javaClass.simpleName}" }
        layoutChildren(container, old?.children)
    }

    fun layoutRecursive(container: ViewGroup, old: UiComponent<*>?) {
        layout(container, old)
        val view = findViewIn(container)!!
        children?.forEach { newChild ->
            val oldChild = old?.children?.firstOrNull { it.id == newChild.id }
            newChild.layoutRecursive(newChild.containerOrElse(view), oldChild)
        }
    }

    open fun layoutChildren(
        container: ViewGroup,
        oldChildren: List<UiComponent<*>>?
    ) {
        if (children != null || oldChildren != null) {
            d { "layout children ${javaClass.simpleName} children $children old $oldChildren" }

            children?.forEach { newChild ->
                val oldChild = oldChildren?.firstOrNull { it.id == newChild.id }
                layoutChild(container, newChild, oldChild)
            }

            oldChildren?.forEach { oldChild ->
                val newChild = children?.firstOrNull { it.id == oldChild.id }
                if (newChild == null) {
                    layoutChild(container, null, oldChild)
                }
            }
        }
    }

    open fun layoutChild(
        container: ViewGroup,
        newChild: UiComponent<*>?,
        oldChild: UiComponent<*>?
    ) {
        d { "layout child ${javaClass.simpleName} new $newChild old $oldChild" }

        val view = findViewIn(container)!!

        if (newChild != null && oldChild == null) {
            newChild.addIfNeeded(newChild.containerOrElse(view))
        } else if (newChild != null && oldChild != null) {
            if (newChild != oldChild) {
                if (newChild.viewType == oldChild.viewType) {
                    newChild.addIfNeeded(newChild.containerOrElse(view))
                } else {
                    newChild.addIfNeeded(newChild.containerOrElse(view))
                    oldChild.removeIfPossible(newChild.containerOrElse(view))
                }
            }
        } else if (newChild == null && oldChild != null) {
            oldChild.removeIfPossible(oldChild.containerOrElse(view))
        }

    }

    fun bindRecursive(container: ViewGroup) {
        val view = findViewIn(container)!!
        bind(view)
        children?.forEach { it.bindRecursive(it.containerOrElse(view)) }
    }

    fun bind(container: ViewGroup) {
        val view = findViewIn(container)!!
        bind(view)
    }

    fun unbind(container: ViewGroup) {
        val view = findViewIn(container)!!
        unbind(view)
    }

    fun rebind(container: ViewGroup) {
        d { "rebind ${javaClass.simpleName}" }
        val view = findViewIn(container)!!
        unbind(view)
        bind(view)
    }

    fun containerContainsThisView(container: ViewGroup): Boolean =
        findViewIn(container) != null

    open fun bind(view: V) {

    }

    open fun unbind(view: V) {
    }

    abstract fun createView(container: ViewGroup): V

    open fun BuildContext.children() {
    }

    protected fun state(vararg state: Any?) {
        this.state.addAll(state)
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

    fun containerOrElse(container: View): ViewGroup =
        containerId?.let { container.findViewById<ViewGroup>(it) } ?: container as ViewGroup

    private fun findViewIn(container: ViewGroup): V? = container.findViewById<V>(viewId)

    override fun toString(): String =
        "Component(id=$id, viewId=$viewId, parent=${parent?.javaClass?.name}, children=$children, containerId=$containerId, state=$state)"

}