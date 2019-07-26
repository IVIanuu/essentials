package com.ivianuu.essentials.sample.ui.widget3.android

import android.view.ViewGroup
import com.ivianuu.essentials.sample.ui.widget3.core.BuildContext
import com.ivianuu.essentials.sample.ui.widget3.core.BuildOwner
import com.ivianuu.essentials.sample.ui.widget3.core.Element
import com.ivianuu.essentials.sample.ui.widget3.core.Widget
import com.ivianuu.essentials.util.cast

private class RootWidget(
    val container: ViewGroup,
    child: BuildContext.() -> Unit
) : Widget(children = child) {
    override fun createElement(): Element = RootElement(this)
}

private class RootElement(widget: RootWidget) : Element(widget) {

    override fun didInsertChild(child: Element, index: Int) {
        super.didInsertChild(child, index)
        val container = widget.cast<RootWidget>().container
        val childViewElement = child.getNearestViewElement()
        val childView = childViewElement.createView(container)
        container.addView(childView)
        childViewElement.updateView(childView)
    }

    override fun didUpdateChild(child: Element, oldWidget: Widget) {
        super.didUpdateChild(child, oldWidget)
        val container = widget.cast<RootWidget>().container
        val childView = container.getChildAt(0)
        val childViewElement = child.getNearestViewElement()
        childViewElement.updateView(childView)
    }

    override fun didRemoveChild(child: Element) {
        super.didRemoveChild(child)
        val container = widget.cast<RootWidget>().container
        val childViewElement = child.getNearestViewElement()
        val childView = container.getChildAt(0)
        container.removeView(childView)
        childViewElement.destroyView(childView)
    }

}

class AndroidBuildOwner(
    val container: ViewGroup,
    val child: BuildContext.() -> Unit
) : BuildOwner() {

    private val root = RootWidget(container, child)
        .createElement()
        .mount(this, null)

}