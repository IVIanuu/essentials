package com.ivianuu.essentials.sample.ui.widget3.android

import android.view.View
import android.view.ViewGroup
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.ui.widget3.core.BuildContext
import com.ivianuu.essentials.sample.ui.widget3.core.Element
import com.ivianuu.essentials.sample.ui.widget3.core.Widget
import com.ivianuu.essentials.util.cast

fun ViewWidget(
    createView: (ViewGroup) -> View,
    updateView: (View) -> Unit,
    destroyView: (View) -> Unit
) = object : ViewWidget() {
    override fun createView(container: ViewGroup): View = createView.invoke(container)
    override fun updateView(view: View) {
        updateView.invoke(view)
    }

    override fun destroyView(view: View) {
        destroyView.invoke(view)
    }
}

abstract class ViewWidget(
    key: Any? = null,
    children: (BuildContext.() -> Unit)? = null
) : Widget(key, children) {

    abstract fun createView(container: ViewGroup): View

    abstract fun updateView(view: View)

    abstract fun destroyView(view: View)

    override fun createElement(): ViewElement = ViewElement(this)
}

open class ViewElement(widget: ViewWidget) : Element(widget) {

    open fun createView(container: ViewGroup): View =
        widget.cast<ViewWidget>().createView(container)
            .also { d { "${widget.id} create view" } }

    open fun updateView(view: View) {
        widget.cast<ViewWidget>().updateView(view)
    }

    open fun destroyView(view: View) {
        widget.cast<ViewWidget>().destroyView(view)
    }

}