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

inline fun <reified V : ViewGroup> BuildContext.ViewGroupWidget(
    children: List<Widget>,
    key: Any? = null,
    noinline updateView: UpdateView<V>? = null
) = ViewGroupWidget(V::class, children, key, updateView)

fun <V : ViewGroup> BuildContext.ViewGroupWidget(
    type: KClass<V>,
    children: List<Widget>,
    key: Any? = null,
    updateView: UpdateView<V>? = null
) = ViewGroupWidget(
    viewType = type,
    children = children,
    key = key,
    createView = {
        type.java.getDeclaredConstructor(Context::class.java)
            .newInstance((+AndroidContextAmbient))
    },
    updateView = updateView
)

inline fun <reified V : ViewGroup> BuildContext.ViewGroupWidget(
    children: List<Widget>,
    key: Any? = null,
    noinline createView: CreateView<V>,
    noinline updateView: UpdateView<V>? = null
) = ViewGroupWidget(
    viewType = V::class,
    children = children,
    key = key,
    createView = createView,
    updateView = updateView
)

fun <V : ViewGroup> BuildContext.ViewGroupWidget(
    viewType: KClass<V>,
    children: List<Widget>,
    key: Any? = null,
    createView: CreateView<V>,
    updateView: UpdateView<V>? = null
): Widget = object : ViewGroupWidget<V>(children = children, key = joinKey(viewType, key)) {
    override fun createView(context: BuildContext): V = createView.invoke(context)
    override fun updateView(context: BuildContext, view: V) {
        super.updateView(context, view)
        updateView?.invoke(context, view)
    }
}

abstract class ViewGroupWidget<V : ViewGroup>(
    val children: List<Widget>,
    key: Any? = null
) : ViewWidget<V>(key) {
    override fun createElement(): ViewGroupElement<V> = ViewGroupElement(this)
}

open class ViewGroupElement<V : ViewGroup>(
    widget: ViewGroupWidget<V>
) : ViewElement<V>(widget) {

    var children = mutableListOf<Element>()
        protected set

    override fun mount(
        parent: Element?,
        slot: Int?
    ) {
        super.mount(parent, slot)
        widget<ViewGroupWidget<V>>().children
            .map { ContainerAmbient.Provider(requireView(), child = it) }
            .forEachIndexed { i, childWidget ->
                val child = childWidget.createElement()
                children.add(child)
                child.mount(this, i)
            }
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
        children = updateChildren(children, widget<ViewGroupWidget<V>>().children
            .map { ContainerAmbient.Provider(value = requireView(), child = it) })
            .toMutableList()
    }

    override fun onEachChild(block: (Element) -> Unit) {
        children.forEach(block)
    }

}