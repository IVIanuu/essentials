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

/**
class ListView(
    override val id: Any?,
    private val buildListComponents: BuildContext.() -> Unit
) : UiComponent<RecyclerView>() {

override val viewId: Int
        get() = R.id.es_recycler_view

override fun layoutChildren(container: ViewGroup, oldChildren: List<UiComponent<*>>?) {
super.layoutChildren(container, oldChildren)
val view = findViewIn(container)!!
        val epoxyController =
            view.tag<UiComponentEpoxyController>(R.id.es_recycler_view)

val newData = children?.map { newChildrenNode ->
            val oldChildrenNode = oldChildren?.firstOrNull {
                it.id == newChildrenNode.id
            }

            ComponentWithPrev(
                newChildrenNode as UiComponent<View>,
                oldChildrenNode as? UiComponent<View>
            )
        }

        epoxyController.setData(newData)
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

private data class ComponentWithPrev(
    val component: UiComponent<View>,
    val prev: UiComponent<View>?
)

private class UiComponentEpoxyController :
TypedEpoxyController<List<ComponentWithPrev>>() {
    override fun buildModels(data: kotlin.collections.List<ComponentWithPrev>?) {
        data?.forEach {
            add(UiComponentEpoxyModel(it))
        }
    }
}

private data class UiComponentEpoxyModel(
    private val componentWithPrev: ComponentWithPrev
) : SimpleModel(id = componentWithPrev.component.id) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        d { "epoxy bind $componentWithPrev" }
        componentWithPrev.component.bind(holder.root)
    }

    override fun unbind(holder: EsHolder) {
        super.unbind(holder)
        d { "epoxy unbind ${componentWithPrev.component}" }
        componentWithPrev.component.unbind(holder.root)
    }

    override fun getViewType(): Int =
        componentWithPrev.component.viewType

    override fun buildView(parent: ViewGroup): View {
        val view = componentWithPrev.component.createView(parent)

        return view
    }

}*/