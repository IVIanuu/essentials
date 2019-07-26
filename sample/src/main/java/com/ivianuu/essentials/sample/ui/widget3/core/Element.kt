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

package com.ivianuu.essentials.sample.ui.widget3.core

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.github.ajalt.timberkt.d

open class Element(widget: Widget) : BuildContext() { // todo do not inhere from build context

    var owner: BuildOwner? = null
        private set
    var parent: Element? = null
        private set

    var widget = widget
        private set

    val children: List<Element>? get() = _children
    private var _children: MutableList<Element>? = null
    private var pendingChildren: MutableList<Widget>? = null

    private var isDirty = false

    open fun mount(owner: BuildOwner, parent: Element?) {
        this.parent = parent
        this.owner = owner

        pendingChildren = mutableListOf()
        widget.children?.invoke(this)
        pendingChildren
            ?.map { it.createElement() }
            ?.forEachIndexed { i, child ->
                child.mount(owner, this)
                if (_children == null) _children = mutableListOf()
                willInsertChild(child, i)
                _children!!.add(child)
                didInsertChild(child, i)
            }
        pendingChildren = null
    }

    open fun unmount() {
        _children?.forEach { it.unmount() }
        _children = null
        parent = null
        owner = null
        isDirty = false
    }

    override fun emit(id: Any, widget: Widget) {
        check(pendingChildren!!.none { it.canUpdate(widget) }) {
            "Cannot add the a widget with the same identity id: ${widget.id} key: ${widget.key}"
        }

        widget.id = id
        pendingChildren!!.add(widget)
    }

    open fun update(newWidget: Widget) {
        widget = newWidget

        // todo add fast paths for new is null old not etc
        widget.children?.let {
            pendingChildren = mutableListOf()
            it.invoke(this)
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getNewListSize(): Int = pendingChildren?.size ?: 0

            override fun getOldListSize(): Int = _children?.size ?: 0

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                _children!![oldItemPosition].widget.id == pendingChildren!![newItemPosition].id

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean =
                _children!![oldItemPosition].widget == pendingChildren!![newItemPosition]

        })

        result.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                if (_children == null) _children = mutableListOf()

                (position until position + count).forEach { i ->
                    val childWidget = pendingChildren!![i]
                    val child = childWidget.createElement()
                    child.mount(owner!!, this@Element)
                    willInsertChild(child, i)
                    _children!!.add(i, child)
                    didInsertChild(child, i)
                }
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                (position until position + count).forEach { i ->
                    val child = _children!![i]
                    val newChildWidget = pendingChildren!![i]
                    willUpdateChild(child, newChildWidget)
                    child.update(newChildWidget)
                    didUpdateChild(child, newChildWidget)
                }
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                val child = _children!![fromPosition]
                willMoveChild(child, fromPosition, toPosition)
                _children!!.remove(child)
                _children!!.add(child)
                didMoveChild(child, fromPosition, toPosition)
            }

            override fun onRemoved(position: Int, count: Int) {
                (position until position + count).forEach { i ->
                    val child = _children!![i]
                    willRemoveChild(child)
                    _children!!.remove(child)
                    didRemoveChild(child)
                    child.unmount()
                }
            }
        })

        pendingChildren = null

        if (_children?.isEmpty() == true) _children = null
        isDirty = false
    }

    fun markNeedsBuild() {
        if (!isDirty) {
            isDirty = true
            owner!!.scheduleBuildFor(this)
        }
    }

    fun rebuild() {
        d { "${widget.id} rebuild is dirty $isDirty" }
        if (isDirty) {
            update(widget)
        }
    }

    protected open fun willInsertChild(child: Element, index: Int) {

    }

    protected open fun didInsertChild(child: Element, index: Int) {
    }

    protected open fun willUpdateChild(child: Element, newWidget: Widget) {

    }

    protected open fun didUpdateChild(child: Element, oldWidget: Widget) {

    }

    protected open fun willMoveChild(child: Element, from: Int, to: Int) {

    }

    protected open fun didMoveChild(child: Element, from: Int, to: Int) {
    }

    protected open fun willRemoveChild(child: Element) {
    }

    protected open fun didRemoveChild(child: Element) {
    }

}