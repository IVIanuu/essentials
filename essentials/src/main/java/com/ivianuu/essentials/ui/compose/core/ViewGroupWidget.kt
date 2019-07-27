package com.ivianuu.essentials.ui.compose.core

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.ivianuu.essentials.util.cast

abstract class ViewGroupWidget : Widget() {

    private val views = mutableListOf<ViewGroup>()
    private val childrenByViews = mutableMapOf<View, Widget>()

    final override fun createView(container: ViewGroup): View {
        val view = createViewGroup(container)
        views.add(view.cast())

        children.forEach { child ->
            val childView = child.createView(view)
            childrenByViews[childView] = child
            view.addView(childView)
        }

        return view
    }

    protected abstract fun createViewGroup(container: ViewGroup): ViewGroup

    override fun updateView(view: View) {
        super.updateView(view)
        val childViews = view.cast<ViewGroup>().children.toList()
        childViews.forEach { childView ->
            val child = childrenByViews[childView]
            child!!.updateView(childView)
        }
    }

    override fun destroyView(view: View) {
        super.destroyView(view)
        val childViews = view.cast<ViewGroup>().children.toList()
        childViews.forEach { childView ->
            view.cast<ViewGroup>().removeView(childView)
            val childViewElement = childrenByViews.remove(childView)!!
            childViewElement.destroyView(childView)
        }
        view.cast<ViewGroup>().removeAllViews()
        views.remove(view.cast())
    }

    override fun didInsertChild(index: Int, child: Widget) {
        super.didInsertChild(index, child)
        views.forEach {
            val view = child.createView(it)
            childrenByViews[view] = child
            it.addView(view)
        }
    }

    override fun didMoveChild(child: Widget, from: Int, to: Int) {
        super.didMoveChild(child, from, to)
        views.forEach {
            val childView = it.getChildAt(from)
            it.removeView(childView)
            it.addView(childView, to)
        }
    }

    override fun willRemoveChild(index: Int, child: Widget) {
        super.willRemoveChild(index, child)
        views.forEach {
            val childView = it.getChildAt(index)
            it.removeView(childView)
            childrenByViews.remove(childView)
            child.destroyView(childView)
        }
    }

}