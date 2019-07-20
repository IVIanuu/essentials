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

    var widget: Widget = widget
        protected set
    var context: Context? = null
        protected set
    var parent: Element? = null
        protected set
    var owner: BuildOwner? = null
        protected set
    var slot: Int? = null
        protected set

    var inheritedWidgets: MutableMap<KClass<out InheritedWidget>, InheritedElement>? = null
        protected set

    var isDirty = false
        protected set

    override fun <T : InheritedWidget> ancestorInheritedElementForWidgetOfExactType(type: KClass<T>): T? =
        inheritedWidgets?.get(type)?.widget as? T

    open fun mount(context: Context, parent: Element?, slot: Int?) {
        d { "${javaClass.simpleName} mount parent $parent slot $slot" }
        this.context = context
        this.parent = parent
        this.owner = parent?.owner
        this.slot = slot
        updateInheritance()
    }

    open fun unmount() {
        d { "${javaClass.simpleName} unmount parent $parent slot $slot" }
        context = null
        parent = null
        slot = null
        inheritedWidgets = null
    }

    open fun attachView() {
    }

    open fun detachView() {
    }

    protected open fun updateInheritance() {
        d { "${javaClass.simpleName} update inheritance ${parent?.inheritedWidgets}" }
        inheritedWidgets = parent?.inheritedWidgets
    }

    open fun rebuild(context: Context) {
        d { "${javaClass.simpleName} rebuild is dirty $isDirty" }
        if (isDirty) {
            performRebuild(context)
        }
    }

    protected open fun performRebuild(context: Context) {

    }

    open fun markNeedsBuild() {
        if (!isDirty) {
            isDirty = true
            owner!!.scheduleBuildFor(this)
        }
    }

    protected open fun updateChild(
        context: Context,
        child: Element?,
        newWidget: Widget?,
        newSlot: Int?
    ): Element? {
        d { "${javaClass.simpleName} update child $child new $newWidget" }

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
                child.update(context, newWidget)
                return child
            }

            child.detachView()
            child.unmount()
        }

        return inflateWidget(context, newWidget, newSlot)
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

    open fun update(context: Context, newWidget: Widget) {
        d { "${javaClass.simpleName} update $newWidget" }
        widget = newWidget
    }

    open fun updateSlot(newSlot: Int?) {
        d { "${javaClass.simpleName} update slow $newSlot" }
        slot = newSlot
    }

    protected open fun inflateWidget(context: Context, newWidget: Widget, newSlot: Int?): Element {
        d { "${javaClass.simpleName} inflate widget $newWidget $newSlot" }

        val newChild = newWidget.createElement()
        newChild.mount(context, this, newSlot)
        newChild.attachView()
        return newChild
    }

    open fun onEachChild(block: (Element) -> Unit) {
    }

    inline fun <reified T : Widget> widget(): T = widget as T

}