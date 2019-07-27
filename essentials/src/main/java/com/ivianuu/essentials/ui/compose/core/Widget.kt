package com.ivianuu.essentials.ui.compose.core

import android.view.View
import android.view.ViewGroup

abstract class Widget<V : View> : WidgetParent {

    var parent: Widget<*>? = null
        private set
    val children = mutableListOf<Widget<*>>()

    open fun mount(parent: Widget<*>?) {
        this.parent = parent
    }

    open fun unmount() {
        children.forEach { it.unmount() }
        this.parent = null
    }

    override fun insertChild(index: Int, child: Widget<*>) {
        willInsertChild(index, child)
        children.add(index, child)
        child.mount(this)
        didInsertChild(index, child)
    }

    override fun moveChild(from: Int, to: Int, count: Int) {
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

    override fun removeChild(index: Int, count: Int) {
        for (i in index + count - 1 downTo index) {
            val child = children[i]
            willRemoveChild(i, child)
            children.removeAt(i)
            child.unmount()
            didRemoveChild(i, child)
        }
    }

    override fun updateChild(child: Widget<*>) {
    }

    protected open fun willInsertChild(index: Int, child: Widget<*>) {
    }

    protected open fun didInsertChild(index: Int, child: Widget<*>) {
    }

    protected open fun willMoveChild(child: Widget<*>, from: Int, to: Int) {
    }

    protected open fun didMoveChild(child: Widget<*>, from: Int, to: Int) {
    }

    protected open fun willRemoveChild(index: Int, child: Widget<*>) {
    }

    protected open fun didRemoveChild(index: Int, child: Widget<*>) {
    }

    abstract fun createView(container: ViewGroup): V

    open fun updateView(view: V) {
    }

    open fun destroyView(view: V) {
    }

}