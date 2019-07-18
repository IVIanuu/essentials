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

abstract class ContainerUiComponent<V : ViewGroup> : UiComponent<V>() {

    override fun layout(view: V) {
        super.layout(view)
        val oldChildren = view.properties.get<List<UiComponent<*>>>("children")
        view.properties.set("children", children)
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
                ?.filterIsInstance<UiComponent<View>>()
                ?.map { it to it.findViewIn(view)!! }
                ?.forEach { it.first.layout(it.second) }
        }
    }

    override fun bind(view: V) {
        super.bind(view)
        children
            ?.filterIsInstance<UiComponent<View>>()
            ?.map { it to it.findViewIn(view)!! }
            ?.forEach { (child, childView) ->
                child.bind(childView)
            }
    }

    override fun unbind(view: V) {
        super.unbind(view)
        children
            ?.filterIsInstance<UiComponent<View>>()
            ?.map { it to it.findViewIn(view)!! }
            ?.forEach { (child, childView) ->
                child.unbind(childView)
            }
    }

    private fun layoutChild(
        view: V,
        newChild: UiComponent<*>?,
        oldChild: UiComponent<*>?
    ) {
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

    private fun UiComponent<*>.addIfNeeded(container: ViewGroup) {
        var view = findViewIn(container)
        if (view == null) {
            view = createView(container)
            container.addView(view)
        }
    }

    private fun UiComponent<*>.removeIfPossible(container: ViewGroup) {
        val view = findViewIn(container)
        if (view != null) {
            container.removeView(view)
        }
    }

}