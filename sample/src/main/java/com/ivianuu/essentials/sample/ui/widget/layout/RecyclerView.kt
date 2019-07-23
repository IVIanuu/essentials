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

package com.ivianuu.essentials.sample.ui.widget.layout

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.ui.widget.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ComponentElement
import com.ivianuu.essentials.sample.ui.widget.lib.Element
import com.ivianuu.essentials.sample.ui.widget.lib.ViewElement
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.ui.epoxy.EsHolder
import com.ivianuu.essentials.ui.epoxy.SimpleModel
import com.ivianuu.essentials.util.cast
import com.ivianuu.kommon.core.view.tag

class RecyclerViewWidget(
    val children: List<Widget>,
    val layoutManager: RecyclerView.LayoutManager? = null,
    key: Any? = null
) : ViewWidget<RecyclerView>(key) {

    override fun createElement() =
        RecyclerViewElement(this)

    override fun createView(
        context: BuildContext
    ) = EpoxyRecyclerView(AndroidContextAmbient(context)).apply {
        val epoxyController =
            WidgetEpoxyController(context.cast())
        adapter = epoxyController.adapter
        tag = epoxyController
    }

    override fun updateView(context: BuildContext, view: RecyclerView) {
        super.updateView(context, view)
        d { "update list view $children" }
        view.tag<WidgetEpoxyController>().setData(children)
        view.layoutManager = layoutManager ?: LinearLayoutManager(AndroidContextAmbient(context))
    }
}

class RecyclerViewElement(widget: RecyclerViewWidget) :
    ViewElement<RecyclerView>(widget) {
    override fun insertChildView(view: View, slot: Int?) {
    }

    override fun moveChildView(view: View, slot: Int?) {
    }

    override fun removeChildView(view: View) {
    }

}

private class WidgetEpoxyController(val element: Element) : TypedEpoxyController<List<Widget>>() {
    override fun buildModels(data: List<Widget>?) {
        d { "build models $data" }
        data?.forEach {
            add(WidgetEpoxyModel(element, it))
        }
    }
}

private data class WidgetEpoxyModel(
    private val parent: Element,
    private val widget: Widget
) : SimpleModel(id = widget.key) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        val element = holder.root.tag<Element>()
        element.update(widget)
    }

    override fun getViewType(): Int = widget::class.hashCode()

    override fun buildView(parent: ViewGroup): View {
        val element = widget.createElement()
        element.mount(this.parent, null)

        // todo this is hacky
        val view: View = if (element is ViewElement<*>) {
            element.view!!
        } else {
            var childElement = element
            while (childElement is ComponentElement) {
                childElement = childElement.child!!
            }

            (childElement as ViewElement<*>).view!!
        }

        view.tag = element

        d { "build view with element $element $view" }

        return view
    }

}