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

package com.ivianuu.essentials.sample.ui.widget.lib

import android.view.View
import android.view.ViewGroup
import com.github.ajalt.timberkt.d

abstract class Widget<V : View> : BuildContext {

    val type: Any = javaClass
    open val key: Any? = null

    abstract val viewId: Int

    override var parent: Widget<*>? = null
    var children: MutableList<Widget<*>>? = null

    var containerId: Int? = null

    private var state: MutableList<Any?>? = null

    open fun layout(view: V) {
        d { "layout ${javaClass.simpleName} -> ${view.javaClass.simpleName}" }
    }

    open fun bind(view: V) {
        d { "bind ${javaClass.simpleName} -> ${view.javaClass.simpleName}" }
    }

    open fun unbind(view: V) {
        d { "unbind ${javaClass.simpleName} -> ${view.javaClass.simpleName}" }
    }

    abstract fun createView(container: ViewGroup): V

    open fun children() {
    }

    protected fun state(vararg state: Any?) {
        if (this.state == null) this.state = mutableListOf()
        this.state!!.addAll(state)
    }

    fun containerOrElse(container: View): ViewGroup =
        containerId?.let { container.findViewById<ViewGroup>(it) } ?: container as ViewGroup

    fun findViewIn(container: ViewGroup): V? = container.findViewById<V>(viewId)

    fun equalsIdentity(other: Widget<*>): Boolean =
        type == other.type && key == other.key

    override fun invalidate() {
        var root: BuildContext = this
        while (root.parent != null) {
            root = root.parent!!
        }

        root.invalidate()
    }

    override fun emit(widget: Widget<*>, containerId: Int?) {
        // todo check duplicate
        d { "emit ${javaClass.simpleName} -> ${widget.javaClass.simpleName}" }
        if (children == null) children = mutableListOf()
        widget.parent = this
        widget.containerId = containerId
        widget.children()
        children!!.add(widget)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Widget<*>) return false

        if (type != other.type) return false
        if (key != other.key) return false
        if (viewId != other.viewId) return false
        if (children != other.children) return false
        if (containerId != other.containerId) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (key?.hashCode() ?: 0)
        result = 31 * result + viewId
        result = 31 * result + (children?.hashCode() ?: 0)
        result = 31 * result + (containerId ?: 0)
        result = 31 * result + (state?.hashCode() ?: 0)
        return result
    }


}