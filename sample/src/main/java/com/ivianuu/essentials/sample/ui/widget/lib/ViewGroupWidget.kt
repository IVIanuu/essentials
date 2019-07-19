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

abstract class ViewGroupWidget<V : ViewGroup> : Widget<V>() {

    override fun layout(view: V) {
        super.layout(view)
        val oldChildren = view.properties.get<List<Widget<*>>>("children")
        view.properties.set("children", children)

        d { "children $children old children $oldChildren" }

        if (children != null || oldChildren != null) {
            // remove old children
            oldChildren?.forEach { oldChild ->
                val newChild = children?.firstOrNull {
                    it.equalsIdentity(oldChild)
                }
                if (newChild == null) {
                    layoutChild(view, null, oldChild)
                }
            }

            // add new children
            children?.forEach { newChild ->
                val oldChild = oldChildren?.firstOrNull {
                    it.equalsIdentity(newChild)
                }
                layoutChild(view, newChild, oldChild)
            }

            // layout children
            children
                ?.filterIsInstance<Widget<View>>()
                ?.map { it to view.findViewByWidget(it)!! }
                ?.forEach { it.first.layout(it.second) }
        }
    }

    private fun layoutChild(
        view: V,
        newChild: Widget<*>?,
        oldChild: Widget<*>?
    ) {
        d { "${javaClass.simpleName} layout child | new ${newChild?.javaClass?.simpleName} old ${oldChild?.javaClass?.simpleName}" }
        if (newChild != null && oldChild == null) {
            d { "${javaClass.simpleName} new not null, old null -> add new if needed" }
            newChild.addIfNeeded(view.findContainerForWidget(newChild))
        } else if (newChild != null && oldChild != null) {
            if (newChild.equalsIdentity(oldChild) &&
                newChild.containerId == oldChild.containerId
            ) {
                d { "${javaClass.simpleName} both not null, same container, same identity -> add new if needed" }
                newChild.addIfNeeded(view.findContainerForWidget(newChild))
            } else {
                d { "${javaClass.simpleName} both not null, not same container or identity -> add new if needed, remove old if possible" }
                removeChildView(view, oldChild)
                newChild.addIfNeeded(view.findContainerForWidget(newChild))
            }
        } else if (newChild == null && oldChild != null) {
            d { "${javaClass.simpleName} new null, old not null -> remove old if possible" }
            removeChildView(view, oldChild)
        }
    }

    protected open fun getChildLayoutParams(
        container: ViewGroup,
        view: View
    ): ViewGroup.LayoutParams? = view.layoutParams

    protected open fun addChildView(container: ViewGroup, view: View) {
        val lp = getChildLayoutParams(container, view)
        if (lp != null) {
            container.addView(view, lp)
        } else {
            container.addView(view)
        }
    }

    protected open fun removeChildView(
        view: ViewGroup,
        widget: Widget<*>
    ) {
        val container = view.findContainerForWidget(widget)
        val childView = container.findViewByWidget(widget)
        d { "${javaClass.simpleName} remove child ${widget.javaClass.simpleName}" }
        container.removeView(childView)
    }

    private fun Widget<*>.addIfNeeded(container: ViewGroup) {
        var view = container.findViewByWidget(this)
        d { "add if needed ${javaClass.simpleName} ${view == null}" }
        if (view == null) {
            view = createView(container)
            view.setWidget(this)
            addChildView(container, view)
        }
    }

    override fun bind(view: V) {
        super.bind(view)
        children?.forEach {
            check(view.findViewByWidget(it) != null) {
                "${javaClass.simpleName} View for children ${it.javaClass.simpleName} is not added but should be"
            }
        }

        children
            ?.filterIsInstance<Widget<View>>()
            ?.map { it to view.findViewByWidget(it)!! }
            ?.forEach { (child, childView) ->
                child.bind(childView)
            }
    }

}