/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui.widget2.lib

import android.content.Context
import android.view.View
import com.github.ajalt.timberkt.d

abstract class ViewWidget<V : View>(key: Any? = null) : Widget(key) {
    override fun createElement(): ViewElement<V> = ViewElement(this)

    abstract fun createView(context: BuildContext, androidContext: Context): V

    open fun updateView(context: BuildContext, view: V) {
    }
}

open class ViewElement<V : View>(widget: ViewWidget<V>) : Element(widget) {

    var view: V? = null
        private set

    private var ancestorViewElement: ViewElement<*>? = null

    open fun insertChildView(view: View, slot: Int?) {
        error("unsupported")
    }

    open fun moveChildView(view: View, slot: Int?) {
        error("unsupported")
    }

    open fun removeChildView(view: View) {
        error("unsupported")
    }

    override fun mount(context: Context, parent: Element?, slot: Int?) {
        super.mount(context, parent, slot)
        view = widget<ViewWidget<V>>().createView(this, context)
        attachView()
    }

    override fun attachView() {
        val ancestorViewElement = findAncestorViewElement()!!
        d { "${javaClass.simpleName} attach to $ancestorViewElement view is $view" }
        this.ancestorViewElement = ancestorViewElement
        ancestorViewElement.insertChildView(view!!, slot)
    }

    override fun detachView() {
        d { "${javaClass.simpleName} remove from $ancestorViewElement view is $view" }
        if (ancestorViewElement != null) {
            ancestorViewElement!!.removeChildView(view!!)
            ancestorViewElement = null
        }
    }

    override fun update(context: Context, newWidget: Widget) {
        super.update(context, newWidget)
        widget<ViewWidget<V>>().updateView(this, view!!)
        isDirty = false
    }

    override fun updateSlot(newSlot: Int?) {
        super.updateSlot(newSlot)
        ancestorViewElement!!.moveChildView(requireView(), slot)
    }

    override fun unmount() {
        super.unmount()
        view = null
    }

    private fun findAncestorViewElement(): ViewElement<*>? {
        var ancestor = parent
        while (ancestor != null) {
            if (ancestor is ViewElement<*>) return ancestor
            ancestor = ancestor.parent
        }

        return null
    }

    protected fun requireView(): V = this.view ?: error("not mounted")

    override fun performRebuild(context: Context) {
        widget<ViewWidget<V>>().updateView(this, view!!)
        isDirty = false
    }

    protected open fun updateChildren(
        context: Context,
        oldChildren: List<Element>,
        newWidgets: List<Widget>
    ): List<Element> {
        var newChildrenTop = 0
        var oldChildrenTop = 0
        var newChildrenBottom = newWidgets.lastIndex
        var oldChildrenBottom = oldChildren.lastIndex

        val newChildren = mutableListOf<Element>()

        // Update the top of the list.
        while ((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = oldChildren[oldChildrenTop]
            val newWidget = newWidgets[newChildrenTop]
            if (!newWidget.canUpdate(oldChild.widget)) break
            val newChild = updateChild(context, oldChild, newWidget, newChildrenTop)!!
            newChildren.add(newChildrenTop, newChild)
            newChildrenTop += 1
            oldChildrenTop += 1
        }

        // Scan the bottom of the list.
        while ((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = oldChildren[oldChildrenTop]
            val newWidget = newWidgets[newChildrenBottom]
            if (!newWidget.canUpdate(oldChild.widget)) break
            oldChildrenBottom -= 1
            newChildrenBottom -= 1
        }

        // Scan the old children in the middle of the list.
        val haveOldChildren = oldChildrenTop <= oldChildrenBottom
        val oldKeyedChildren = mutableMapOf<Any, Element>()

        if (haveOldChildren) {
            while (oldChildrenTop <= oldChildrenBottom) {
                val oldChild = oldChildren[oldChildrenTop]
                if (oldChild.widget.key != null) {
                    oldKeyedChildren[oldChild.widget.key!!] = oldChild
                } else {
                    oldChild.detachView()
                    oldChild.unmount()
                }
                oldChildrenTop += 1
            }
        }

        while (newChildrenTop <= newChildrenBottom) {
            var oldChild: Element? = null
            val newWidget = newWidgets[newChildrenTop]
            if (haveOldChildren) {
                val key = newWidget.key
                if (key != null) {
                    oldChild = oldKeyedChildren[key]
                    if (oldChild != null) {
                        if (newWidget.canUpdate(oldChild.widget)) {
                            // we found a match!
                            // remove it from oldKeyedChildren so we don't unsync it later
                            oldKeyedChildren.remove(key)
                        } else {
                            // Not a match, let's pretend we didn't see it for now.
                            oldChild = null
                        }
                    }
                }
            }

            val newChild = updateChild(context, oldChild, newWidget, newChildrenTop)!!
            newChildren.add(newChildrenTop, newChild)
            newChildrenTop += 1
        }

        // We've scanned the whole list.
        newChildrenBottom = newWidgets.lastIndex
        oldChildrenBottom = oldChildren.lastIndex

        // Update the bottom of the list.
        while ((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = oldChildren[oldChildrenTop]
            val newWidget = newWidgets[newChildrenTop]
            val newChild = updateChild(context, oldChild, newWidget, newChildrenTop)!!
            newChildren.add(newChildrenTop, newChild)
            newChildrenTop += 1
            oldChildrenTop += 1
        }

        return newChildren
    }

}