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
        val oldChildren = view.childrenWidgets

        if (children != oldChildren) {
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

            view.childrenWidgets = children

            // layout children
            children
                ?.filterIsInstance<Widget<View>>()
                ?.map { it to view.findViewByWidget(it)!! }
                ?.forEach { it.first.dispatchLayout(it.second) }
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
            addChildView(view, newChild)
        } else if (newChild != null && oldChild != null) {
            if (newChild.equalsIdentity(oldChild) &&
                newChild.containerId == oldChild.containerId
            ) {
                d { "${javaClass.simpleName} both not null, same container, same identity -> add" }
                addChildView(view, newChild)
            } else {
                d { "${javaClass.simpleName} both not null, not same container or identity -> add new, remove old" }
                removeChildView(view, oldChild)
                addChildView(view, newChild)
            }
        } else if (newChild == null && oldChild != null) {
            d { "${javaClass.simpleName} new null, old not null -> remove old" }
            removeChildView(view, oldChild)
        }
    }

    protected open fun getChildLayoutParams(
        container: ViewGroup,
        view: View,
        widget: Widget<*>
    ): ViewGroup.LayoutParams? = view.layoutParams

    protected open fun addChildView(view: V, widget: Widget<*>) {
        val container = view.findContainerForWidget(widget)
        var childView = container.findViewByWidget(widget)
        d { "add if needed ${javaClass.simpleName} ${childView == null}" }
        if (childView == null) {
            childView = widget.createView(container)
            childView.setWidget(widget)
            val lp = getChildLayoutParams(container, childView, widget)
            if (lp != null) {
                container.addView(childView, lp)
            } else {
                container.addView(childView)
            }
        }
    }

    protected open fun removeChildView(view: V, widget: Widget<*>) {
        d { "${javaClass.simpleName} remove child ${widget.javaClass.simpleName}" }
        val container = view.findContainerForWidget(widget)
        val childView = container.findViewByWidget(widget)
        if (childView != null) container.removeView(childView)
    }

    override fun bind(view: V) {
        super.bind(view)

        children
            ?.filterIsInstance<Widget<View>>()
            ?.map { it to view.findViewByWidget(it)!! }
            ?.forEach { (child, childView) ->
                child.dispatchBind(childView)
            }
    }
}