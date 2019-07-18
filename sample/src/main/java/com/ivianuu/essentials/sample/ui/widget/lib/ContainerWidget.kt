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

abstract class ContainerWidget<V : ViewGroup> : Widget<V>() {

    override fun layout(view: V) {
        super.layout(view)
        val oldChildren = view.properties.get<List<Widget<*>>>("children")
        view.properties.set("children", children)

        d { "children $children old children $oldChildren" }

        if (children != null || oldChildren != null) {
            children?.forEach { newChild ->
                val oldChild = oldChildren?.firstOrNull { it.id == newChild.id }
                layoutChild(view, newChild, oldChild)
            }

            oldChildren?.forEach { oldChild ->
                val newChild = children?.firstOrNull { it.id == oldChild.id }
                if (newChild == null) {
                    layoutChild(view, null, oldChild)
                }
            }

            children
                ?.filterIsInstance<Widget<View>>()
                ?.map { it to it.findViewIn(view)!! }
                ?.forEach { it.first.layout(it.second) }
        }
    }

    private fun layoutChild(
        view: V,
        newChild: Widget<*>?,
        oldChild: Widget<*>?
    ) {
        if (newChild != null && oldChild == null) {
            newChild.addIfNeeded(newChild.containerOrElse(view))
        } else if (newChild != null && oldChild != null) {
            if (newChild.viewType == oldChild.viewType) {
                newChild.addIfNeeded(newChild.containerOrElse(view))
            } else {
                oldChild.removeIfPossible(newChild.containerOrElse(view))
                newChild.addIfNeeded(newChild.containerOrElse(view))
            }
        } else if (newChild == null && oldChild != null) {
            oldChild.removeIfPossible(oldChild.containerOrElse(view))
        }
    }

    private fun Widget<*>.addIfNeeded(container: ViewGroup) {
        var view = findViewIn(container)
        d { "add if needed ${javaClass.simpleName} ${view == null}" }
        if (view == null) {
            view = createView(container)
            container.addView(view)
        }
    }

    private fun Widget<*>.removeIfPossible(container: ViewGroup) {
        val view = findViewIn(container)
        d { "remove if possible ${javaClass.simpleName} ${view != null}" }
        if (view != null) {
            container.removeView(view)
        }
    }

    override fun bind(view: V) {
        super.bind(view)
        children
            ?.filterIsInstance<Widget<View>>()
            ?.map { it to it.findViewIn(view)!! }
            ?.forEach { (child, childView) ->
                child.bind(childView)
            }
    }

    override fun unbind(view: V) {
        super.unbind(view)
        children
            ?.filterIsInstance<Widget<View>>()
            ?.map { it to it.findViewIn(view)!! }
            ?.forEach { (child, childView) ->
                child.unbind(childView)
            }
    }

}