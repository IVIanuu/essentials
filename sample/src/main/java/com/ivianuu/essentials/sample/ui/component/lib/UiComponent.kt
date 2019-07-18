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

    private val state = mutableListOf<Any?>() // todo lazy init

    fun state(vararg state: Any?) {
        this.state.addAll(state)
    }

    fun addOrUpdate(container: ViewGroup) {
        d { "update ${javaClass.simpleName}" }
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

    fun removeIfPossible(container: ViewGroup) {
        val view: V? = container.findViewById<V>(viewId)
        d { "remove if possible ${javaClass.simpleName} $view" }
        if (view != null) {
            unbind(view)
            container.removeView(view)
        }
    }

    fun buildChildren(buildContext: BuildContext) {
        with(buildContext) { children() }
    }

    fun updateChildrenInternal(
        newChildren: List<ComponentNode>,
        oldChildren: List<ComponentNode>
    ) {
        updateChildren(newChildren, oldChildren)
    }

    protected open fun bind(view: V) {

    }

    protected open fun unbind(view: V) {
    }

    protected abstract fun createView(container: ViewGroup): V

    protected open fun BuildContext.children() {
    }

    protected open fun updateChildren(
        newChildren: List<ComponentNode>,
        oldChildren: List<ComponentNode>
    ) {

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


}