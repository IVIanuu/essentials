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

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.github.ajalt.timberkt.d
import kotlin.reflect.KClass

inline fun <reified V : ViewGroup> ViewGroupWidget(
    key: Any? = null,
    noinline updateView: UpdateView<V>? = null,
    noinline children: BuildContext.() -> Unit
) = ViewGroupWidget(V::class, key, updateView, children)

fun <V : ViewGroup> ViewGroupWidget(
    type: KClass<V>,
    key: Any? = null,
    updateView: UpdateView<V>? = null,
    children: BuildContext.() -> Unit
) = ViewGroupWidget(
    viewType = type,
    key = key,
    createView = {
        type.java.getDeclaredConstructor(Context::class.java)
            .newInstance((it.context))
    },
    updateView = updateView,
    children = children
)

inline fun <reified V : ViewGroup> ViewGroupWidget(
    key: Any? = null,
    noinline createView: CreateView<V>,
    noinline updateView: UpdateView<V>? = null,
    noinline children: BuildContext.() -> Unit
) = ViewGroupWidget<V>(
    viewType = V::class,
    key = key,
    createView = createView,
    updateView = updateView,
    children = children
)

fun <V : ViewGroup> ViewGroupWidget(
    viewType: KClass<V>,
    key: Any? = null,
    createView: CreateView<V>,
    updateView: UpdateView<V>? = null,
    children: BuildContext.() -> Unit
): Widget = object : ViewGroupWidget<V>(key = joinKey(viewType, key), children = children) {
    override fun createView(container: ViewGroup): V = createView.invoke(container)
    override fun updateView(view: V) {
        super.updateView(view)
        updateView?.invoke(view)
    }
}

abstract class ViewGroupWidget<V : ViewGroup>(
    key: Any? = null,
    val children: BuildContext.() -> Unit
) : ViewWidget<V>(key) {
    override fun createElement(): ViewGroupElement<V> = ViewGroupElement(this)
}

open class ViewGroupElement<V : ViewGroup>(
    widget: ViewGroupWidget<V>
) : ViewElement<V>(widget) {

    var children = mutableListOf<Element>()
        protected set
    private val pendingWidgets = mutableListOf<Widget>()

    override fun mount(
        parent: Element?,
        slot: Int?
    ) {
        super.mount(parent, slot)
        widget<ViewGroupWidget<V>>().children(this)

        pendingWidgets.forEach { widget ->
            val element = widget.createElement()
            children.add(element)
            element.mount(this, children.lastIndex)
        }
        pendingWidgets.clear()
    }

    override fun add(child: Widget) {
        pendingWidgets.add(child)
    }

    override fun insertChildView(view: View, slot: Int?) {
        d { "${javaClass.simpleName} insert $view at $slot" }

        if (slot != null) {
            requireView().addView(view, slot)
        } else {
            requireView().addView(view)
        }
    }

    override fun moveChildView(view: View, slot: Int?) {
        requireNotNull(slot)
        d { "${javaClass.simpleName} move $view to $slot" }
        requireView().removeView(view)
        requireView().addView(view, slot)
    }

    override fun removeChildView(view: View) {
        d { "${javaClass.simpleName} remove $view" }
        requireView().removeView(view)
    }

    override fun attachView() {
        super.attachView()
        children.forEach { it.attachView() }
    }

    override fun detachView() {
        children.forEach { it.detachView() }
        super.detachView()
    }

    override fun unmount() {
        children.forEach { it.unmount() }
        children.clear()
        super.unmount()
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        widget<ViewGroupWidget<V>>().children(this)
        children = updateChildren(children, pendingWidgets)
        pendingWidgets.clear()
    }

    override fun onEachChild(block: (Element) -> Unit) {
        children.forEach(block)
    }

}