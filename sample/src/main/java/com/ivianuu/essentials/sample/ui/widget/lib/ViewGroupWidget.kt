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
            children?.forEach { newChild ->
                val oldChild = oldChildren?.firstOrNull {
                    it.equalsIdentity(newChild)
                }
                layoutChild(view, newChild, oldChild)
            }

            oldChildren?.forEach { oldChild ->
                val newChild = children?.firstOrNull {
                    it.equalsIdentity(oldChild)
                }
                if (newChild == null) {
                    layoutChild(view, null, oldChild)
                }
            }

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
        if (newChild != null && oldChild == null) {
            newChild.addIfNeeded(view.findContainerForWidget(newChild))
        } else if (newChild != null && oldChild != null) {
            if (newChild.containerId == oldChild.containerId
                && newChild.equalsIdentity(oldChild)
            ) {
                newChild.addIfNeeded(view.findContainerForWidget(newChild))
            } else {
                oldChild.removeIfPossible(view.findContainerForWidget(oldChild))
                newChild.addIfNeeded(view.findContainerForWidget(newChild))
            }
        } else if (newChild == null && oldChild != null) {
            oldChild.removeIfPossible(view.findContainerForWidget(oldChild))
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

    protected open fun removeChildView(container: ViewGroup, view: View) {
        container.removeView(view)
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

    private fun Widget<*>.removeIfPossible(container: ViewGroup) {
        val view = container.findViewByWidget(this)
        d { "remove if possible ${javaClass.simpleName} ${view != null}" }
        if (view != null) {
            removeChildView(container, view)
        }
    }

    override fun bind(view: V) {
        super.bind(view)
        children
            ?.filterIsInstance<Widget<View>>()
            ?.map { it to view.findViewByWidget(it)!! }
            ?.forEach { (child, childView) ->
                child.bind(childView)
            }
    }

}