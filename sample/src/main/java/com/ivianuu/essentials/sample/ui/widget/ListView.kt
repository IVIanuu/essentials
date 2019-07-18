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

package com.ivianuu.essentials.sample.ui.widget

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.sample.ui.widget.lib.properties
import com.ivianuu.essentials.ui.epoxy.EsHolder
import com.ivianuu.essentials.ui.epoxy.SimpleModel
import com.ivianuu.essentials.util.cast
import com.ivianuu.kommon.core.view.inflate

class ListView(
    override val id: Any?,
    private val buildListComponents: BuildContext.() -> Unit
) : Widget<RecyclerView>() {

    override val viewId: Int
        get() = R.id.es_recycler_view

    override fun bind(view: RecyclerView) {
        super.bind(view)
        val epoxyController =
            view.properties.get<UiComponentEpoxyController>("epoxy_controller")!!
        epoxyController.setData(children)
    }

    override fun unbind(view: RecyclerView) {
        super.unbind(view)
        val epoxyController =
            view.properties.get<UiComponentEpoxyController>("epoxy_controller")!!
        epoxyController.cancelPendingModelBuild()
    }

    override fun createView(container: ViewGroup): RecyclerView {
        val view = container.inflate<RecyclerView>(R.layout.es_view_recycler_view)
        val epoxyController =
            UiComponentEpoxyController()
        view.adapter = epoxyController.adapter
        view.properties.set("epoxy_controller", epoxyController)
        return view
    }

    override fun BuildContext.children() {
        buildListComponents()
    }

}

private class UiComponentEpoxyController :
    TypedEpoxyController<List<Widget<*>>>() {
    override fun buildModels(data: List<Widget<*>>?) {
        data?.forEach {
            add(UiComponentEpoxyModel(it))
        }
    }
}

private data class UiComponentEpoxyModel(
    private val component: Widget<*>
) : SimpleModel(id = component.id) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        d { "epoxy bind ${component.javaClass.simpleName}" }
        component.cast<Widget<View>>().bind(holder.root)
    }

    override fun unbind(holder: EsHolder) {
        super.unbind(holder)
        d { "epoxy unbind ${component.javaClass.simpleName}" }
        component.cast<Widget<View>>().unbind(holder.root)
    }

    override fun getViewType(): Int = component.viewType

    override fun buildView(parent: ViewGroup): View {
        val view = component.createView(parent)
        (component as Widget<View>).layout(view)
        return view
    }

}