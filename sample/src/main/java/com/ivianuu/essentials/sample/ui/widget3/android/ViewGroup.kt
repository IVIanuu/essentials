package com.ivianuu.essentials.sample.ui.widget3.android

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.ivianuu.essentials.sample.ui.widget3.core.BuildContext
import com.ivianuu.essentials.sample.ui.widget3.core.Element
import com.ivianuu.essentials.sample.ui.widget3.core.Widget
import com.ivianuu.essentials.util.cast

fun ViewGroupWidget(
    createView: (ViewGroup) -> ViewGroup,
    updateView: (ViewGroup) -> Unit,
    destroyView: (ViewGroup) -> Unit,
    children: BuildContext.() -> Unit
) = object : ViewGroupWidget(children = children) {
    override fun createView(container: ViewGroup): ViewGroup = createView.invoke(container)
    override fun updateView(view: ViewGroup) {
        updateView.invoke(view)
    }

    override fun destroyView(view: ViewGroup) {
        destroyView.invoke(view)
    }
}

abstract class ViewGroupWidget(
    key: Any? = null,
    children: BuildContext.() -> Unit
) : ViewWidget(key, children) {

    abstract override fun createView(container: ViewGroup): ViewGroup

    final override fun updateView(view: View) {
        updateView(view as ViewGroup)
    }

    abstract fun updateView(view: ViewGroup)

    final override fun destroyView(view: View) {
        destroyView(view as ViewGroup)
    }

    abstract fun destroyView(view: ViewGroup)

    override fun createElement(): ViewGroupElement = ViewGroupElement(this)

}

open class ViewGroupElement(widget: ViewGroupWidget) : ViewElement(widget) {

    private val views = mutableListOf<ViewGroup>()
    private val childViewElementsByViews = mutableMapOf<View, ViewElement>()

    override fun createView(container: ViewGroup): View {
        val view = super.createView(container) as ViewGroup
        views.add(view.cast())

        val childViewElements = children?.map { it.getNearestViewElement() }
        childViewElements?.forEach { childViewElement ->
            val childView = childViewElement.createView(view)
            childViewElementsByViews[childView] = childViewElement
            view.addView(childView)
        }

        return view
    }

    override fun updateView(view: View) {
        super.updateView(view)
        view.cast<ViewGroup>()
            .children
            .toList()
            .forEach { childView ->
                childViewElementsByViews[childView]!!.updateView(childView)
            }
    }

    override fun destroyView(view: View) {
        super.destroyView(view)
        val childViews = view.cast<ViewGroup>().children.toList()
        childViews.forEach { childView ->
            view.cast<ViewGroup>().removeView(childView)
            val childViewElement = childViewElementsByViews.remove(childView)!!
            childViewElement.destroyView(childView)
        }
        view.cast<ViewGroup>().removeAllViews()
        views.remove(view.cast())
    }

    override fun didInsertChild(child: Element, index: Int) {
        super.didInsertChild(child, index)
        val childViewElement = child.getNearestViewElement()
        views.forEach {
            val view = childViewElement.createView(it)
            childViewElementsByViews[view] = childViewElement
            it.addView(view)
        }
    }

    override fun didUpdateChild(child: Element, oldWidget: Widget) {
        super.didUpdateChild(child, oldWidget)
        val childIndex = children!!.indexOf(child)
        val childViewElement = child.getNearestViewElement()
        views.forEach {
            childViewElement.updateView(it.getChildAt(childIndex))
        }
    }

    override fun didMoveChild(child: Element, from: Int, to: Int) {
        super.didMoveChild(child, from, to)
        views.forEach {
            val childView = it.getChildAt(from)
            it.removeView(childView)
            it.addView(childView)
        }
    }

    override fun didRemoveChild(child: Element) {
        super.didRemoveChild(child)
        val childViewElement = child.getNearestViewElement()
        val childIndex = children!!.indexOf(child)
        views.forEach {
            val childView = it.getChildAt(childIndex)
            it.removeView(childView)
            childViewElementsByViews.remove(childView)
            childViewElement.destroyView(childView)
        }
    }

}
