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
import com.github.ajalt.timberkt.d
import kotlin.reflect.KClass

abstract class Widget(val key: Any? = null) {

    abstract fun createElement(): Element

    fun canUpdate(other: Widget): Boolean =
        (this::class == other::class && this.key == other.key)
            .also { d { "${javaClass.simpleName} can update ${other.javaClass.simpleName} = $it" } }

}

abstract class Element(widget: Widget) : BuildContext {

    override var widget: Widget = widget
        protected set
    var context: Context? = null
        protected set
    var parent: Element? = null
        protected set
    override var owner: BuildOwner? = null
        protected set
    var slot: Int? = null
        protected set

    var isDirty = true
        protected set

    override fun <T : Widget> ancestorWidget(
        type: KClass<T>,
        key: Any?
    ): T? {
        d { "get ancestor widget $type" }
        var ancestor = parent
        while (ancestor != null) {
            if (ancestor.widget::class == type && ancestor.widget.key == key) return ancestor.widget as T
            ancestor = ancestor.parent
        }

        return null
    }

    open fun mount(parent: Element?, slot: Int?) {
        d { "${javaClass.simpleName} mount parent $parent widget $widget slot $slot" }
        this.context = context
        this.parent = parent
        this.owner = parent?.owner
        this.slot = slot
    }

    open fun unmount() {
        d { "${javaClass.simpleName} unmount parent $parent widget $widget slot $slot" }
        context = null
        parent = null
        slot = null
    }

    open fun attachView() {
    }

    open fun detachView() {
    }

    open fun rebuild() {
        d { "${javaClass.simpleName} rebuild is dirty $isDirty widget $widget" }
        if (isDirty) {
            performRebuild()
        }
    }

    protected open fun performRebuild() {

    }

    open fun markNeedsBuild() {
        if (!isDirty) {
            isDirty = true
            owner!!.scheduleBuildFor(this)
        }
    }

    protected open fun updateChild(
        child: Element?,
        newWidget: Widget?,
        newSlot: Int?
    ): Element? {
        d { "${javaClass.simpleName} update child $child new $newWidget new slot $newSlot" }

        if (newWidget == null) {
            if (child != null) {
                child.detachView()
                child.unmount()
            }
            return null
        }

        if (child != null) {
            if (child.widget == newWidget) {
                if (child.slot != newSlot) {
                    updateSlotForChild(child, newSlot)
                    return child
                }
            }

            if (child.widget.canUpdate(newWidget)) {
                d { "${javaClass.simpleName} call child update ${child.javaClass.simpleName}" }
                child.update(newWidget)
                return child
            }

            child.detachView()
            child.unmount()
        }

        return inflateWidget(newWidget, newSlot)
    }

    protected open fun updateSlotForChild(child: Element, newSlot: Int?) {
        d { "${javaClass.simpleName} update slot for child $child $newSlot" }
        lateinit var block: (Element) -> Unit

        block = {
            it.updateSlot(newSlot)
            if (it !is ViewElement<*>) it.onEachChild(block)
        }

        block(child)
    }

    open fun update(newWidget: Widget) {
        d { "${javaClass.simpleName} update new $newWidget old $widget" }
        widget = newWidget
    }

    open fun updateSlot(newSlot: Int?) {
        d { "${javaClass.simpleName} update slow $newSlot" }
        slot = newSlot
    }

    protected open fun inflateWidget(
        newWidget: Widget,
        newSlot: Int?
    ): Element {
        d { "${javaClass.simpleName} inflate $newWidget $newSlot" }

        val newChild = newWidget.createElement()
        newChild.mount(this, newSlot)

        return newChild
    }

    open fun onEachChild(block: (Element) -> Unit) {
    }

    inline fun <reified T : Widget> widget(): T = widget as T

}