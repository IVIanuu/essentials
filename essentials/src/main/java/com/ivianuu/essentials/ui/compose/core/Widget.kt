package com.ivianuu.essentials.ui.compose.core

import android.view.View
import android.view.ViewGroup

abstract class Widget {

    var parent: Widget? = null
    val children = mutableListOf<Widget>()

    open fun insertChild(index: Int, child: Widget) {
        willInsertChild(index, child)
        child.parent = this
        children.add(index, child)
        didInsertChild(index, child)
    }

    open fun moveChild(from: Int, to: Int, count: Int) {
        for (i in 0 until count) {
            val fromIndex = if (from > to) from + i else from
            val toIndex = if (from > to) to + i else to + count - 2
            val child = children[fromIndex]

            willMoveChild(child, fromIndex, toIndex)
            children.removeAt(fromIndex)
            children.add(toIndex, child)
            didMoveChild(child, fromIndex, toIndex)
        }
    }

    open fun removeChild(index: Int, count: Int) {
        for (i in index + count - 1 downTo index) {
            val child = children[i]
            willRemoveChild(i, child)
            children.removeAt(i)
            child.parent = null
            didRemoveChild(i, child)
        }
    }

    open fun updateChild(child: Widget) {
    }

    protected open fun willInsertChild(index: Int, child: Widget) {
    }

    protected open fun didInsertChild(index: Int, child: Widget) {
    }

    protected open fun willMoveChild(child: Widget, from: Int, to: Int) {
    }

    protected open fun didMoveChild(child: Widget, from: Int, to: Int) {
    }

    protected open fun willRemoveChild(index: Int, child: Widget) {
    }

    protected open fun didRemoveChild(index: Int, child: Widget) {
    }

    open fun WidgetComposition.compose() {
    }

    abstract fun createView(container: ViewGroup): View

    open fun updateView(view: View) {
    }

    open fun destroyView(view: View) {
    }

}