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
import com.ivianuu.essentials.util.cast

abstract class Widget<V : View> : BuildContext {

    open val key: Any? = null

    override var parent: Widget<*>? = null
    var children: MutableList<Widget<*>>? = null

    var containerId: Int? = null

    private var state: MutableList<Any?>? = null

    open fun dispatchLayout(view: V) {
        d { "${javaClass.simpleName} dispatch layout ${view.laidOutWidget != this}" }
        if (view.laidOutWidget != this) {
            layout(view)
            view.laidOutWidget = this
        }
    }

    open fun layout(view: V) {
        d { "layout ${javaClass.simpleName} -> ${view.javaClass.simpleName}" }
    }

    open fun dispatchBind(view: V) {
        d { "${javaClass.simpleName} dispatch bind ${view.boundWidget != this}" }
        if (view.boundWidget != this) {
            bind(view)
            view.boundWidget = this
        }
    }

    open fun bind(view: V) {
        d { "bind ${javaClass.simpleName} -> ${view.javaClass.simpleName}" }
    }

    abstract fun createView(container: ViewGroup): V

    open fun rebuildChildren() {
        children?.clear()
        buildChildren()
    }

    open fun buildChildren() {
        d { "${javaClass.simpleName} build children" }
    }

    protected fun state(vararg state: Any?) {
        if (this.state == null) this.state = mutableListOf()
        this.state!!.addAll(state)
    }

    fun equalsIdentity(other: Widget<*>): Boolean =
        this::class == other::class && key == other.key

    override fun invalidate() {
        val parentsStack = mutableListOf<BuildContext>(this)
        while (parentsStack.last().parent != null) {
            parentsStack.add(parentsStack.last().parent!!)
        }

        parentsStack.reverse()

        val rootView = parentsStack.first().cast<RootWidget>().rootBuildContext.view

        val view = parentsStack.drop(1).fold<BuildContext, View>(
            rootView
        ) { view, context -> view.findViewByWidget(context.cast())!! }
        rebuildChildren()
        layout(view as V)
        bind(view)
    }

    override fun emit(widget: Widget<*>, containerId: Int?) {
        d { "emit ${javaClass.simpleName} -> ${widget.javaClass.simpleName}" }
        if (children == null) children = mutableListOf()
        widget.parent = this
        widget.containerId = containerId
        widget.rebuildChildren()
        children!!.add(widget)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Widget<*>) return false

        if (key != other.key) return false
        if (state != other.state) return false
        if (containerId != other.containerId) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (key?.hashCode() ?: 0)
        result = 31 * result + (state?.hashCode() ?: 0)
        result = 31 * result + (containerId ?: 0)
        result = 31 * result + (children?.hashCode() ?: 0)
        return result
    }

}