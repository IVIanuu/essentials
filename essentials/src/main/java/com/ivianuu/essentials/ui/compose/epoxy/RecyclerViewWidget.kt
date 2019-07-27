package com.ivianuu.essentials.ui.compose.epoxy

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.Widget
import com.ivianuu.essentials.ui.compose.core.WidgetComposition
import com.ivianuu.essentials.ui.epoxy.EsHolder
import com.ivianuu.essentials.ui.epoxy.SimpleModel
import com.ivianuu.kommon.core.view.getTagOrSet

open class RecyclerViewWidget(private val _children: WidgetComposition.() -> Unit) : Widget() {

    private val View.epoxyController: WidgetEpoxyController
        get() = getTagOrSet {
            WidgetEpoxyController()
                .also {
                    (this as RecyclerView)
                        .adapter = it.adapter
                }
        }

    private val views = mutableSetOf<ViewGroup>()

    override fun createView(container: ViewGroup): View =
        EpoxyRecyclerView(container.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            layoutManager = LinearLayoutManager(container.context)
            epoxyController // todo init
            views.add(this)
        }

    override fun destroyView(view: View) {
        super.destroyView(view)
        view.epoxyController.cancelPendingModelBuild()
        views.remove(view)
    }

    override fun updateView(view: View) {
        super.updateView(view)
        updateModels()
    }

    override fun didInsertChild(index: Int, child: Widget) {
        super.didInsertChild(index, child)
        updateModels()
    }

    override fun didMoveChild(child: Widget, from: Int, to: Int) {
        super.didMoveChild(child, from, to)
        updateModels()
    }

    override fun willRemoveChild(index: Int, child: Widget) {
        super.willRemoveChild(index, child)
        updateModels()
    }

    override fun WidgetComposition.compose() {
        _children()
    }

    private fun updateModels() {
        views.forEach { it.epoxyController.setData(children) }
    }
}

private class WidgetEpoxyController : TypedEpoxyController<List<Widget>>() {
    override fun buildModels(data: List<Widget>?) {
        data?.forEach { WidgetModel(it).addTo(this) }
    }
}

private class WidgetModel(val widget: Widget) :
    SimpleModel(id = widget) { // todo we need unique ids

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        d { "bind $widget to holder $holder" }
        widget.updateView(holder.root)
    }

    override fun buildView(parent: ViewGroup): View {
        val view = widget.createView(parent)
        widget.updateView(view)
        return view
    }


}