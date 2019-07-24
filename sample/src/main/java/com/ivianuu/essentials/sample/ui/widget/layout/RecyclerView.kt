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
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ComponentElement
import com.ivianuu.essentials.sample.ui.widget.lib.Element
import com.ivianuu.essentials.sample.ui.widget.lib.ViewElement
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.ui.epoxy.EsHolder
import com.ivianuu.essentials.ui.epoxy.SimpleModel
import com.ivianuu.kommon.core.view.tag

fun RecyclerView(
    layoutManager: RecyclerView.LayoutManager? = null,
    children: BuildContext.() -> Unit
): Widget = RecyclerViewWidget(layoutManager, children)

private class RecyclerViewWidget(
    val layoutManager: RecyclerView.LayoutManager? = null,
    val children: BuildContext.() -> Unit
) : ViewWidget<RecyclerView>() {

    override fun createElement() =
        RecyclerViewElement(this)

    override fun createView(container: ViewGroup): RecyclerView {
        return EpoxyRecyclerView(container.context).apply {
            val epoxyController =
                WidgetEpoxyController()
            adapter = epoxyController.adapter
            tag = epoxyController
        }
    }

    override fun updateView(view: RecyclerView) {
        super.updateView(view)
        view.tag<WidgetEpoxyController>().setData(children)
        view.layoutManager = layoutManager ?: LinearLayoutManager(view.context)
    }

}

private class RecyclerViewElement(widget: RecyclerViewWidget) : ViewElement<RecyclerView>(widget) {

    val insertedWidgets = mutableListOf<Widget>()

    override fun createView() {
        super.createView()
        requireView().tag<WidgetEpoxyController>().parent = this
    }

    override fun add(child: Widget) {
        insertedWidgets.add(child)
    }

    override fun insertChildView(view: View, slot: Int?) {
    }

    override fun moveChildView(view: View, slot: Int?) {
    }

    override fun removeChildView(view: View) {
    }

}

private class WidgetEpoxyController : TypedEpoxyController<BuildContext.() -> Unit>() {

    lateinit var parent: RecyclerViewElement

    override fun buildModels(data: (BuildContext.() -> Unit)?) {
        data?.invoke(parent)

        val elementsWithViewType = parent.insertedWidgets
            .map { it.createElement() }
            .apply {
                forEachIndexed { i, element ->
                    element.mount(parent, i)
                }
            }
            .map { it to it.getViewType() }

        elementsWithViewType.forEach { (element, viewType) ->
            add(WidgetEpoxyModel(element, viewType))
        }

        parent.insertedWidgets.clear()
    }

    private fun Element.getViewType(): Int {
        var hash = widget::class.hashCode() + widget.key.hashCode()
        onEachChild { hash += it.getViewType() }
        return hash
    }

}

private data class WidgetEpoxyModel(
    private val element: Element,
    private val viewType: Int
) : SimpleModel(id = element.widget) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)

        val viewsByPosition = holder.root.tag<Map<Int, View>>()

        var pos = 0
        element.onEachRecursive {
            d { "child of ${element.widget} -> $it" }
            if (it is ViewElement<*>) {
                val view = viewsByPosition[pos]
                (it as ViewElement<View>).view = view
                it.updateView()
            }

            pos++
        }
    }

    override fun getViewType(): Int = viewType

    override fun buildView(parent: ViewGroup): View {
        element.createView()
        element.attachView()

        val view = if (element is ViewElement<*>) {
            element.view!!
        } else {
            var childElement = element
            while (childElement is ComponentElement) {
                childElement = childElement.child!!
            }

            (childElement as ViewElement<*>).view!!
        }

        val viewsByPosition = mutableMapOf<Int, View>()

        var pos = 0
        element.onEachRecursive {
            if (it is ViewElement<*>) {
                viewsByPosition[pos] = it.view!!
            }

            pos++
        }

        view.tag = viewsByPosition

        return view
    }

}