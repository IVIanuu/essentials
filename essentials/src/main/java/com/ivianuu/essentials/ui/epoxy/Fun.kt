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
    id: Any?,
    layoutRes: Int,
    properties: Array<Any?>? = null,
    unbind: (EsHolder.() -> Unit)? = null,
    bind: (EsHolder.() -> Unit)? = null
): FunModel = FunModel(id, layoutRes, properties, unbind, bind)
    .also { it.addTo(this) }

class FunModel(
    id: Any?,
    private val layoutRes: Int,
    private val properties: Array<Any?>? = null,
    private val unbind: (EsHolder.() -> Unit)? = null,
    private val bind: (EsHolder.() -> Unit)? = null
) : SimpleModel(id = id, layoutRes = layoutRes) {

    override fun bind(holder: EsHolder) {
        super.bind(holder)
        bind?.invoke(holder)
    }

    override fun unbind(holder: EsHolder) {
        super.unbind(holder)
        unbind?.invoke(holder)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FunModel) return false
        if (!super.equals(other)) return false

        if (properties != null) {
            if (other.properties == null) return false
            if (!properties.contentEquals(other.properties)) return false
        } else if (other.properties != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (properties?.contentHashCode() ?: 0)
        return result
    }


}