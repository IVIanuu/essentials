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

package com.ivianuu.essentials.ui.epoxy

import com.airbnb.epoxy.EpoxyModelWithHolder

/**
 * Base list model with holder
 */
abstract class EsModel<H : EsHolder>(
    id: Any? = null,
    private val layoutRes: Int = -1
) : EpoxyModelWithHolder<H>() {

    private val listeners = mutableListOf<ModelListener>()

    init {
        id(id.hashCode())
        layout(layoutRes)
    }

    override fun getDefaultLayout() = layoutRes

    override fun bind(holder: H) {
        super.bind(holder)
        listeners.toList().forEach { it.onBind(this, holder) }
    }

    override fun unbind(holder: H) {
        super.unbind(holder)
        listeners.toList().forEach { it.onUnbind(this, holder) }
    }

    fun addModelListener(listener: ModelListener) {
        listeners += listener
    }

    fun removeModelListener(listener: ModelListener) {
        listeners -= listener
    }
}

fun EsModel<*>.doOnBind(block: (EsModel<out EsHolder>, EsHolder) -> Unit): ModelListener =
    addModelListener(onBind = block)

fun EsModel<*>.doOnUnbind(block: (EsModel<out EsHolder>, EsHolder) -> Unit): ModelListener =
    addModelListener(onUnbind = block)

fun EsModel<*>.addModelListener(
    onBind: ((EsModel<out EsHolder>, EsHolder) -> Unit)? = null,
    onUnbind: ((EsModel<out EsHolder>, EsHolder) -> Unit)? = null
): ModelListener {
    return object : ModelListener {
        override fun onBind(model: EsModel<out EsHolder>, holder: EsHolder) {
            onBind?.invoke(model, holder)
        }

        override fun onUnbind(model: EsModel<out EsHolder>, holder: EsHolder) {
            onUnbind?.invoke(model, holder)
        }
    }.also { addModelListener(it) }
}

interface ModelListener {
    fun onBind(model: EsModel<out EsHolder>, holder: EsHolder) {
    }

    fun onUnbind(model: EsModel<out EsHolder>, holder: EsHolder) {
    }
}
