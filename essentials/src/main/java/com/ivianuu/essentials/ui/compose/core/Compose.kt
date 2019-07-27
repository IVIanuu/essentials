package com.ivianuu.essentials.ui.compose.core

import android.view.View
import android.view.ViewGroup
import androidx.compose.Component
import androidx.compose.Composer
import androidx.compose.CompositionContext
import androidx.compose.currentComposerNonNull

fun ViewGroup.setViewContent(composable: WidgetComposition.() -> Unit) {
    var root = getRootComponent() as? Root
    if (root == null) {
        removeAllViews()
        root = Root(this)
        root.composable = composable
        setRoot(root)
        val cc = CompositionContext.prepare(
            root,
            null
        ) { WidgetComposer(root, this) }
        root.composer = cc
        root.update()
    } else {
        root.composable = composable
        root.update()
    }
}

fun ViewGroup.disposeComposition() {
    // temporary easy way to call correct lifecycles on everything
    // need to remove compositionContext from context map as well
    setViewContent { }
    setTag(TAG_ROOT_COMPONENT, null)
}

private class Root(val view: ViewGroup) : Component(), WidgetParent {
    fun update() = composer.compose()

    lateinit var composable: WidgetComposition.() -> Unit
    lateinit var composer: CompositionContext

    private var child: Widget<*>? = null

    @Suppress("PLUGIN_ERROR")
    override fun compose() {
        val cc = currentComposerNonNull
        cc.startGroup(0)
        with(WidgetComposition(cc as Composer<Any>)) { composable() }
        cc.endGroup()
    }

    override fun insertChild(index: Int, child: Widget<*>) {
        this.child = child
        child.mount(null)
        val childView = child.createView(view)
        view.addView(childView)
        updateChild(child)
    }

    override fun moveChild(from: Int, to: Int, count: Int) {
        error("unsupported")
    }

    override fun removeChild(index: Int, count: Int) {
        val childView = view.getChildAt(0)
        view.removeViewAt(index)
        (child!! as Widget<View>).destroyView(childView)
        child!!.unmount()
        child = null
    }

    override fun updateChild(child: Widget<*>) {
        (child as Widget<View>).updateView(view.getChildAt(0))
    }

}

private val TAG_ROOT_COMPONENT = "composeRootComponent".hashCode()

private fun View.getRootComponent(): Component? = getTag(TAG_ROOT_COMPONENT) as? Component

private fun View.setRoot(component: Component) {
    setTag(TAG_ROOT_COMPONENT, component)
}
