package com.ivianuu.essentials.ui.compose.core

import android.view.View
import android.view.ViewGroup
import androidx.compose.Component
import androidx.compose.Composer
import androidx.compose.CompositionContext
import androidx.compose.currentComposerNonNull

object Compose {

    internal class Root(val view: ViewGroup) : Component(), WidgetParent {
        fun update() = composer.compose()

        lateinit var composable: WidgetComposition.() -> Unit
        lateinit var composer: CompositionContext

        private var child: Widget? = null

        @Suppress("PLUGIN_ERROR")
        override fun compose() {
            val cc = currentComposerNonNull
            cc.startGroup(0)
            with(WidgetComposition(cc as Composer<Any>)) { composable() }
            cc.endGroup()
        }

        override fun insertChild(index: Int, child: Widget) {
            this.child = child
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
            child!!.destroyView(childView)
            child = null
        }

        override fun updateChild(child: Widget) {
            child.updateView(view.getChildAt(0))
        }

    }

    private val TAG_ROOT_WIDGET = "composeRootComponent".hashCode()

    private fun getRootComponent(view: View): Component? {
        return view.getTag(TAG_ROOT_WIDGET) as? Component
    }

    private fun setRoot(view: View, component: Component) {
        view.setTag(TAG_ROOT_WIDGET, component)
    }

    fun composeInto(
        container: ViewGroup,
        composable: WidgetComposition.() -> Unit
    ) {
        var root = getRootComponent(container) as? Root
        if (root == null) {
            container.removeAllViews()
            root = Root(container)
            root.composable = composable
            setRoot(container, root)
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

    fun disposeComposition(container: ViewGroup) {
        // temporary easy way to call correct lifecycles on everything
        // need to remove compositionContext from context map as well
        composeInto(container) { }
        container.setTag(TAG_ROOT_WIDGET, null)
    }

}

fun ViewGroup.setViewContent(composable: WidgetComposition.() -> Unit) =
    Compose.composeInto(this, composable)

fun ViewGroup.disposeComposition() = Compose.disposeComposition(this)