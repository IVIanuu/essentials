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

package com.ivianuu.essentials.sample.ui.widget.builder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.ivianuu.essentials.sample.ui.widget.lib.*
import kotlin.properties.Delegates

typealias BuildView<V> = ViewWidgetBuilder<V>.() -> Unit
typealias BuildViewGroup<V> = ViewGroupWidgetBuilder<V>.() -> Unit

inline fun <reified V : View> BuildContext.View(
    block: BuildView<V>
): Widget = ViewWidgetBuilder<V>()
    .apply {
        createView {
            V::class.java.getDeclaredConstructor(Context::class.java)
                .newInstance((it.context))
        }
    }
    .apply(block)
    .build()

open class ViewWidgetBuilder<V : View> {

    protected var createView by Delegates.notNull<CreateView<V>>()
    protected val updateViewBlocks = mutableListOf<UpdateView<V>>()
    protected val updateLayoutParamsBlocks = mutableListOf<UpdateLayoutParams>()

    fun createView(block: CreateView<V>) {
        createView = block
    }

    fun updateView(block: UpdateView<V>) {
        updateViewBlocks.add(block)
    }

    fun updateLayoutParams(block: UpdateLayoutParams) {
        updateLayoutParamsBlocks.add(block)
    }

    open fun build(): Widget =
        _Widget<V>(createView, updateViewBlocks, updateLayoutParamsBlocks)

    private class _Widget<V : View>(
        private val createView: CreateView<V>,
        private val updateViewBlocks: List<UpdateView<V>>,
        private val updateLayoutParamsBlocks: List<UpdateLayoutParams>
    ) : StatelessWidget() {
        override fun BuildContext.child() {
            +LayoutParamsWidget(
                updateLayoutParams = { lp ->
                    var updated = false
                    updateLayoutParamsBlocks.forEach { updated = it(lp) || updated }
                    updated
                },
                child = {
                    +ViewWidget<V>(
                        createView = createView,
                        updateView = { v -> updateViewBlocks.forEach { it(v) } }
                    )
                }
            )
        }
    }

}

inline fun <reified V : ViewGroup> BuildContext.ViewGroup(block: BuildViewGroup<V>): Widget {
    return ViewGroupWidgetBuilder<V>()
        .apply {
            createView {
                V::class.java.getDeclaredConstructor(Context::class.java)
                    .newInstance(it.context)
            }
        }
        .apply(block)
        .build()
}

inline fun <V : ViewGroup> BuildContext.ViewGroup(
    dummy: Any? = null,
    block: BuildViewGroup<V>
): Widget = ViewGroupWidgetBuilder<V>().apply(block).build()

open class ViewGroupWidgetBuilder<V : ViewGroup> : ViewWidgetBuilder<V>() {

    private val children = mutableListOf<BuildContext.() -> Unit>()

    fun children(block: BuildContext.() -> Unit) {
        children.add(block)
    }

    override fun build(): Widget =
        _Widget<V>(createView, updateViewBlocks, updateLayoutParamsBlocks, children)

    private class _Widget<V : ViewGroup>(
        private val createView: CreateView<V>,
        private val updateViewBlocks: List<UpdateView<V>>,
        private val updateLayoutParamsBlocks: List<UpdateLayoutParams>,
        private val children: List<BuildContext.() -> Unit>
    ) : StatelessWidget() {
        override fun BuildContext.child() {
            +LayoutParamsWidget(
                updateLayoutParams = { lp ->
                    var updated = false
                    updateLayoutParamsBlocks.forEach { updated = it(lp) || updated }
                    updated
                },
                child = {
                    +ViewGroupWidget<V>(
                        createView = createView,
                        updateView = { v -> updateViewBlocks.forEach { it(v) } },
                        children = {
                            println("build view group children")
                            children.forEach { it() }
                        }
                    )
                }
            )
        }
    }

}