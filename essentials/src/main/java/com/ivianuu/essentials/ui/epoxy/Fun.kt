/*
 * Copyright 2018 Manuel Wrage
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

import com.airbnb.epoxy.EpoxyController

fun EpoxyController.model(
    id: Any? = null,
    layoutRes: Int = 0,
    state: Array<Any?>? = null,
    unbind: (EsHolder.() -> Unit)? = null,
    bind: (EsHolder.() -> Unit)? = null,
    block: (FunModelBuilder.() -> Unit)? = null
): FunModel = FunModelBuilder()
    .apply {
        id(id)
        layoutRes(layoutRes)
        state?.let { state(*it) }
        bind?.let { bind(it) }
        unbind?.let { unbind(it) }
        block?.invoke(this)
    }
    .build()
    .also { it.addTo(this) }

class FunModelBuilder internal constructor() {

    private var id: Any? = null
    private var layoutRes: Int = 0
    private val state = mutableListOf<Any?>()
    private val bindActions = mutableListOf<EsHolder.() -> Unit>()
    private val unbindActions = mutableListOf<EsHolder.() -> Unit>()

    fun id(id: Any?) {
        this.id = id
    }

    fun layoutRes(layoutRes: Int) {
        this.layoutRes = layoutRes
    }

    fun state(vararg state: Any?) {
        this.state.addAll(state)
    }

    fun state(state: Iterable<Any?>) {
        this.state.addAll(state)
    }

    fun bind(block: EsHolder.() -> Unit) {
        bindActions.add(block)
    }

    fun unbind(block: EsHolder.() -> Unit) {
        unbindActions.add(block)
    }

    internal fun build(): FunModel = FunModel(id, layoutRes, state, bindActions, unbindActions)

}

class FunModel internal constructor(
    id: Any?,
    private val layoutRes: Int,
    private val state: List<Any?>,
    private val unbindActions: List<EsHolder.() -> Unit>,
    private val bindActions: List<EsHolder.() -> Unit>
) : SimpleModel(id = id, layoutRes = layoutRes) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        bindActions.forEach { it(holder) }
    }

    override fun unbind(holder: EsHolder) {
        super.unbind(holder)
        unbindActions.forEach { it(holder) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FunModel) return false
        if (!super.equals(other)) return false

        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }

}