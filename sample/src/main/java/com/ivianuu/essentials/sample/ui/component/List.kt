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

package com.ivianuu.essentials.sample.ui.component

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.component.lib.BuildContext
import com.ivianuu.essentials.sample.ui.component.lib.UiComponent
import com.ivianuu.essentials.ui.epoxy.EsHolder
import com.ivianuu.essentials.ui.epoxy.SimpleModel
import com.ivianuu.kommon.core.view.inflate
import com.ivianuu.kommon.core.view.tag

class List(
    override val id: Any?,
    private val buildListComponents: BuildContext.() -> Unit
) : UiComponent<RecyclerView>() {

    override val viewId: Int
        get() = R.id.es_recycler_view

    override fun layoutChildren(
        view: RecyclerView,
        newChildren: kotlin.collections.List<UiComponent<*>>?,
        oldChildren: kotlin.collections.List<UiComponent<*>>?
    ) {
        val epoxyController =
            view.tag<UiComponentEpoxyController>(R.id.es_recycler_view)
        epoxyController.setData(newChildren)
    }

    override fun createView(container: ViewGroup): RecyclerView {
        val view = container.inflate<RecyclerView>(R.layout.es_view_recycler_view)
        val epoxyController =
            UiComponentEpoxyController()
        view.adapter = epoxyController.adapter
        view.setTag(R.id.es_recycler_view, epoxyController)
        return view
    }

    override fun BuildContext.children() {
        buildListComponents()
    }

}

private class UiComponentEpoxyController :
    TypedEpoxyController<kotlin.collections.List<UiComponent<*>>>() {
    override fun buildModels(data: kotlin.collections.List<UiComponent<*>>?) {
        data?.forEach {
            add(UiComponentEpoxyModel(it as UiComponent<View>))
        }
    }
}

private data class UiComponentEpoxyModel(
    private val component: UiComponent<View>
) : SimpleModel(id = component.id) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        d { "epoxy bind $component" }
        component.bind(holder.root)
    }

    override fun unbind(holder: EsHolder) {
        super.unbind(holder)
        d { "epoxy unbind $component" }
        component.unbind(holder.root)
    }

    override fun getViewType(): Int = component.viewType

    override fun buildView(parent: ViewGroup): View {
        return component.createView(parent)
    }

}