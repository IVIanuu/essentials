package com.ivianuu.essentials.ui.compose.epoxy

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.ivianuu.essentials.ui.compose.core.Widget
import com.ivianuu.essentials.ui.epoxy.EsHolder
import com.ivianuu.essentials.ui.epoxy.SimpleModel
import com.ivianuu.kommon.core.view.getTagOrSet

open class RecyclerViewWidget : Widget<RecyclerView>() {

    private val RecyclerView.epoxyController: WidgetEpoxyController
        get() = getTagOrSet {
            WidgetEpoxyController()
                .also { adapter = it.adapter }
        }

    private val views = mutableSetOf<RecyclerView>()

    override fun createView(container: ViewGroup) = EpoxyRecyclerView(container.context).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        layoutManager = LinearLayoutManager(container.context)
        epoxyController // todo init
        views.add(this)
    }

    override fun destroyView(view: RecyclerView) {
        super.destroyView(view)
        view.epoxyController.cancelPendingModelBuild()
        views.remove(view)
    }

    override fun updateView(view: RecyclerView) {
        super.updateView(view)
        updateModels()
    }

    override fun didInsertChild(index: Int, child: Widget<*>) {
        super.didInsertChild(index, child)
        updateModels()
    }

    override fun didMoveChild(child: Widget<*>, from: Int, to: Int) {
        super.didMoveChild(child, from, to)
        updateModels()
    }

    override fun willRemoveChild(index: Int, child: Widget<*>) {
        super.willRemoveChild(index, child)
        updateModels()
    }

    private fun updateModels() {
        views.forEach { it.epoxyController.setData(children) }
    }
}

private class WidgetEpoxyController : TypedEpoxyController<List<Widget<*>>>() {
    override fun buildModels(data: List<Widget<*>>?) {
        data?.forEach { WidgetModel(it).addTo(this) }
    }
}

private class WidgetModel(val widget: Widget<*>) :
    SimpleModel(id = widget) { // todo we need unique ids

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        (widget as Widget<View>).updateView(holder.root)
    }

    override fun buildView(parent: ViewGroup): View {
        val view = widget.createView(parent)
        (widget as Widget<View>).updateView(view)
        return view
    }


}