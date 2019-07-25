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
import com.ivianuu.essentials.sample.ui.widget.lib.CreateView
import com.ivianuu.essentials.sample.ui.widget.lib.LayoutParamsWidget
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.UpdateLayoutParams
import com.ivianuu.essentials.sample.ui.widget.lib.UpdateView
import com.ivianuu.essentials.sample.ui.widget.lib.ViewWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import kotlin.properties.Delegates
import kotlin.reflect.KClass

inline fun <reified V : View> View(block: ViewWidgetBuilder<V>.() -> Unit): Widget {
    return View(V::class) {
        createView {
            V::class.java.getDeclaredConstructor(Context::class.java)
                .newInstance(it.context)
        }
        block()
    }
}

inline fun <V : View> View(
    viewType: KClass<V>,
    block: ViewWidgetBuilder<V>.() -> Unit
): Widget = ViewWidgetBuilder(viewType).apply(block).build()

open class ViewWidgetBuilder<V : View>(private val viewType: KClass<V>) {

    private var createView by Delegates.notNull<CreateView<V>>()
    private val updateViewBlocks = mutableListOf<UpdateView<V>>()
    private val updateLayoutParamsBlocks = mutableListOf<UpdateLayoutParams>()

    fun createView(block: CreateView<V>) {
        createView = block
    }

    fun updateView(block: UpdateView<V>) {
        updateViewBlocks.add(block)
    }

    fun updateLayoutParams(block: UpdateLayoutParams) {
        updateLayoutParamsBlocks.add(block)
    }

    fun build(): Widget = StatelessWidget("ViewWidgetBuilder") {
        +LayoutParamsWidget(
            props = emptyList(),
            updateLayoutParams = { lp ->
                var updated = false
                updateLayoutParamsBlocks.forEach { updated = it(lp) || updated }
                updated
            },
            child = {
                +ViewWidget(
                    viewType = viewType,
                    createView = createView,
                    updateView = { v -> updateViewBlocks.forEach { it(v) } }
                )
            }
        )
    }
}