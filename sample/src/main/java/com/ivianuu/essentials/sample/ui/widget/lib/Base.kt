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

package com.ivianuu.essentials.sample.ui.widget.lib

import com.github.ajalt.timberkt.d

abstract class Widget(val key: Any? = null) {

    var id: Any? = null

    abstract fun createElement(): Element

    fun canUpdate(other: Widget): Boolean = (this.id == other.id && this.key == other.key)
        .also {
            println("$id can update ${other.id} ? $it")
        }

}

abstract class Element(widget: Widget) : BuildContext() {

    override var widget: Widget = widget
        protected set
    var parent: Element? = null
        protected set
    override var owner: BuildOwner? = null
        protected set
    var slot: Int? = null
        protected set

    var isDirty = true
        protected set
    var isAttached = false
        protected set
    var isViewCreated = false
        protected set

    protected var dependencies: MutableList<Element>? = null
    protected var dependents: MutableList<Element>? = null

    private var effects: MutableMap<Any, EffectState<*>>? = null
    private var lifecycleObservers: MutableList<LifecycleObserver>? = null
    private var notifiedActive = false

    final override fun add(id: Any, child: Widget) {
        child.id = id
        add(child)
    }

    protected open fun add(child: Widget) {
        error("unsupported")
    }

    override fun <T> getAmbient(key: Ambient<T>): T? {
        var ancestor: Element? = this
        while (ancestor != null) {
            if (ancestor.widget is Ambient<*>.Provider
                && (ancestor.widget as Ambient<T>.Provider).ambientKey == key.key
            ) {
                if (ancestor.dependents == null) ancestor.dependents = mutableListOf()
                ancestor.dependents!!.add(this)

                if (dependencies == null) dependencies = mutableListOf()
                dependencies!!.add(ancestor)

                val provider = ancestor.widget as Ambient<T>.Provider

                return provider.value
            }
            ancestor = ancestor.parent
        }

        return null
    }

    override fun <T> cache(id: Any, calculation: () -> T): T =
        cacheImpl(id, null, calculation)

    override fun <T> cache(id: Any, vararg inputs: Any?, calculation: () -> T): T =
        cacheImpl(id, inputs.toList(), calculation)

    private fun <T> cacheImpl(id: Any, inputs: List<Any?>?, calculation: () -> T): T {
        if (effects == null) effects = mutableMapOf()

        val result: T

        val prevState = effects!!.get(id)

        when {
            prevState == null -> {
                result = calculation()
                if (result is LifecycleObserver) {
                    if (lifecycleObservers == null) lifecycleObservers = mutableListOf()
                    lifecycleObservers!!.add(result)
                }
                effects!![id] = EffectState(inputs, result)
            }
            prevState.inputs == inputs -> result = prevState.result as T
            else -> {
                result = calculation()
                effects!![id] = EffectState(inputs, result)
            }
        }

        return result
    }

    open fun mount(parent: Element?, slot: Int?) {
        d { "${widget.id} mount parent $parent widget $widget slot $slot" }
        this.parent = parent
        this.owner = parent?.owner
        this.slot = slot
    }

    open fun unmount() {
        d { "${widget.id} unmount parent $parent widget $widget slot $slot" }

        lifecycleObservers?.forEach { it.onDispose() }
        lifecycleObservers = null

        parent = null
        slot = null
        owner = null
        isDirty = false

        effects = null

        // todo make api
        dependents = null
        dependencies?.forEach { it.dependents?.remove(this) }
        dependencies = null
    }

    open fun createView() {
        isViewCreated = true
    }

    open fun destroyView() {
        isViewCreated = false
    }

    open fun attachView() {
        isAttached = true
    }

    open fun detachView() {
        isAttached = false
    }

    open fun rebuild() {
        d { "${widget.id} rebuild is dirty $isDirty" }
        if (isDirty) {
            performRebuild()
            if (!notifiedActive) {
                notifiedActive = true
                lifecycleObservers?.forEach { it.onActive() }
            }
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
        d { "${widget.id} update child $child new ${newWidget?.key} new slot $newSlot" }

        if (newWidget == null) {
            if (child != null) {
                child.detachView()
                child.destroyView()
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
                d { "${widget.id} call child update ${child.widget.id}" }
                child.update(newWidget)
                return child
            }

            child.detachView()
            child.destroyView()
            child.unmount()
        }

        return inflateWidget(newWidget, newSlot)
    }

    protected open fun updateSlotForChild(child: Element, newSlot: Int?) {
        d { "${widget.id} update slot for child $child $newSlot" }
        lateinit var block: (Element) -> Unit

        block = {
            it.updateSlot(newSlot)
            if (it !is ViewElement<*>) it.onEachChild(block)
        }

        block(child)
    }

    open fun update(newWidget: Widget) {
        d { "${widget.id} update new $newWidget old $widget" }
        widget = newWidget
    }

    open fun updateSlot(newSlot: Int?) {
        d { "${widget.id} update slow $newSlot" }
        slot = newSlot
    }

    protected open fun inflateWidget(
        newWidget: Widget,
        newSlot: Int?
    ): Element {
        d { "${widget.id} inflate $newWidget $newSlot" }

        val newChild = newWidget.createElement()
        newChild.mount(this, newSlot)
        if (isViewCreated) newChild.createView()
        if (isAttached) newChild.attachView()

        return newChild
    }

    open fun onEachChild(block: (Element) -> Unit) {
    }

    fun onEachRecursive(block: (Element) -> Unit) {
        block(this)
        onEachChild { it.onEachRecursive(block) }
    }

    inline fun <reified T : Widget> widget(): T = widget as T

    protected open fun updateChildren(
        oldChildren: List<Element>,
        newWidgets: List<Widget>
    ): MutableList<Element> {
        d { "${widget.id} update children" }
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
            val newChild = updateChild(oldChild, newWidget, newChildrenTop)!!
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

        // Scan the old child in the middle of the list.
        val haveOldChildren = oldChildrenTop <= oldChildrenBottom
        val oldKeyedChildren = mutableMapOf<Any, Element>()

        if (haveOldChildren) {
            while (oldChildrenTop <= oldChildrenBottom) {
                val oldChild = oldChildren[oldChildrenTop]
                if (oldChild.widget.key != null) {
                    oldKeyedChildren[oldChild.widget.key!!] = oldChild
                } else {
                    oldChild.detachView()
                    oldChild.destroyView()
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

            val newChild = updateChild(oldChild, newWidget, newChildrenTop)!!
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
            val newChild = updateChild(oldChild, newWidget, newChildrenTop)!!
            newChildren.add(newChildrenTop, newChild)
            newChildrenTop += 1
            oldChildrenTop += 1
        }

        return newChildren
    }

}