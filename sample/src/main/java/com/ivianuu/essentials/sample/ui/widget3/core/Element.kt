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

abstract class Element(widget: Widget) : BuildContext() { // todo do not inhere from build context

    var owner: BuildOwner? = null
        private set
    var parent: Element? = null
        private set

    var widget = widget
        private set

    val children: List<Element>? get() = _children
    private var _children: MutableList<Element>? = null
    private var pendingChildren: MutableList<Widget>? = null

    open fun mount(owner: BuildOwner, parent: Element?) {
        this.parent = parent
        this.owner = owner

        pendingChildren = mutableListOf()
        widget.children?.invoke(this)
        pendingChildren
            ?.map { it.createElement() }
            ?.forEach {
                it.mount(owner, this)
                if (_children == null) _children = mutableListOf()
                _children!!.add(it)
            }
        pendingChildren = null
    }

    open fun unmount() {
        _children?.forEach { it.unmount() }
        _children = null
        this.parent = null
        this.owner = null
    }

    override fun emit(id: Any, widget: Widget) {
        check(pendingChildren!!.none { it.canUpdate(widget) }) {
            "Cannot add the a widget with the same identity id: ${widget.id} key: ${widget.key}"
        }

        widget.id = id
        pendingChildren!!.add(widget)
    }

    open fun update(newWidget: Widget) {
        val oldWidget = widget
        widget = newWidget

        if (newWidget.children != oldWidget.children) {
            pendingChildren = mutableListOf()
            widget.children?.invoke(this)

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
                override fun onChanged(position: Int, count: Int, payload: Any?) {
                    (position until position + count).forEach {
                        children!![it].update(pendingChildren!![it])
                    }
                }

                override fun onInserted(position: Int, count: Int) {
                    if (_children == null) _children = mutableListOf()

                    (position until position + count).forEach { i ->
                        _children!!.add(i, pendingChildren!![i]
                            .createElement().also { child ->
                                child.mount(owner!!, this@Element)
                            }
                        )
                    }
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    _children!!.add(toPosition, _children!!.removeAt(fromPosition))
                }

                override fun onRemoved(position: Int, count: Int) {
                    (position until position + count).forEach {
                        _children!!.removeAt(it).unmount()
                    }
                }
            })

            pendingChildren = null

            if (_children?.isEmpty() == true) _children = null
        }
    }

}