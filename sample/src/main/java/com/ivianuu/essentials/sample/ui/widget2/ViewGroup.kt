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

package com.ivianuu.essentials.sample.ui.widget2

import android.content.Context
import android.view.View
import android.view.ViewGroup

abstract class ViewGroupWidget<V : ViewGroup>(
    val children: List<Widget>
) : ViewWidget<V>() {
    abstract override fun createElement(): ViewGroupElement<V>
}

open class ViewGroupElement<V : ViewGroup>(
    widget: ViewGroupWidget<V>
) : ViewElement<V>(widget) {

    private var children = mutableListOf<Element>()

    override fun mount(context: Context, parent: Element?, slot: Int?) {
        super.mount(context, parent, slot)
        widget<ViewGroupWidget<V>>().children.forEach {
            val child = it.createElement()
            children.add(child)
            child.mount(context, this, null)
        }
    }

    override fun insertChild(view: View, slot: Int?) {
        if (slot != null) {
            requireView().addView(view, slot)
        } else {
            requireView().removeView(view)
        }
    }

    override fun moveChild(view: View, slot: Int) {
        requireView().removeView(view)
        requireView().addView(view, slot)
    }

    override fun removeChild(view: View) {
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

    override fun update(context: Context, newWidget: Widget) {
        super.update(context, newWidget)
        children = updateChildren(context, children, widget<ViewGroupWidget<V>>().children)
            .toMutableList()
    }
}