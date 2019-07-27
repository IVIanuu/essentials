package com.ivianuu.essentials.ui.compose.core

import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import androidx.compose.Composer
import androidx.compose.FrameManager
import androidx.compose.Recomposer

object Compose {

    private class Root(val view: ViewGroup) : Widget() {
        fun update() {
            val composerWasComposing = composer.isComposing
            try {
                composer.isComposing = true
                composer.startRoot()
                with(WidgetComposition(composer)) { compose() }
                composer.endRoot()
                composer.applyChanges()
                FrameManager.nextFrame()
            } finally {
                composer.isComposing = composerWasComposing
            }
        }

        lateinit var composable: WidgetComposition.() -> Unit
        lateinit var composer: Composer<Widget>

        override fun WidgetComposition.compose() {
            composer.startGroup(0)
            composable()
            composer.endGroup()
        }

        override fun createView(container: ViewGroup): View = view

        override fun didInsertChild(index: Int, child: Widget) {
            super.didInsertChild(index, child)
            val childView = child.createView(view)
            view.addView(childView)
            updateChild(child)
        }

        override fun willRemoveChild(index: Int, child: Widget) {
            val childView = view.getChildAt(index)
            view.removeViewAt(index)
            child.destroyView(childView)
            super.willRemoveChild(index, child)
        }

        override fun updateChild(child: Widget) {
            super.updateChild(child)
            val childView = view.getChildAt(children.indexOf(child))
            child.updateView(childView)
        }
    }

    private val TAG_ROOT_WIDGET = "composeRootComponent".hashCode()

    private fun getRootWidget(view: View): Widget? {
        return view.getTag(TAG_ROOT_WIDGET) as? Widget
    }

    private fun setRoot(view: View, widget: Widget) {
        view.setTag(TAG_ROOT_WIDGET, widget)
    }

    fun composeInto(
        container: ViewGroup,
        composable: WidgetComposition.() -> Unit
    ) {
        var root = getRootWidget(container) as? Root
        if (root == null) {
            container.removeAllViews()
            root = Root(container)
            root.composable = composable
            setRoot(container, root)
            val cc = WidgetComposer(root, AndroidRecomposer())
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

private class AndroidRecomposer : Recomposer() {

    private var frameScheduled = false

    private val frameCallback = Choreographer.FrameCallback {
        frameScheduled = false
        dispatchRecomposes()
    }

    override fun scheduleChangesDispatch() {
        if (!frameScheduled) {
            frameScheduled = true
            Choreographer.getInstance().postFrameCallback(frameCallback)
        }
    }

    override fun hasPendingChanges(): Boolean = frameScheduled
}