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

package com.ivianuu.essentials.ui.list.common

import androidx.recyclerview.widget.GridLayoutManager
import com.ivianuu.essentials.ui.list.ListAdapter
import com.ivianuu.essentials.ui.list.ListController
import com.ivianuu.essentials.ui.list.ListModel
import com.ivianuu.essentials.ui.list.getModelAt
import com.ivianuu.essentials.ui.list.getProperty
import com.ivianuu.essentials.ui.list.setProperty

private const val KEY_SPAN_SIZE_OVERRIDE = "ListModelSpanSizeLookUp.spanSizeOverride"

/**
 * Callback to provide the span size of a specific [ListModel]
 */
interface SpanSizeCallback {
    fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int
}

class ListModelSpanSizeLookUp(
    private val adapter: ListAdapter,
    private val layoutManager: GridLayoutManager
) : GridLayoutManager.SpanSizeLookup() {

    var defaultSpanSize = 1

    init {
        isSpanIndexCacheEnabled = true
    }

    constructor(
        controller: ListController,
        layoutManager: GridLayoutManager
    ) : this(controller.adapter, layoutManager)

    override fun getSpanSize(position: Int): Int {
        val model = adapter.getModelAt(position)
        val spanCount = layoutManager.spanCount
        val itemCount = adapter.itemCount
        val spanSizeOverride = model.getProperty<SpanSizeCallback>(KEY_SPAN_SIZE_OVERRIDE)
        return spanSizeOverride?.getSpanSize(spanCount, position, itemCount)
            ?: (model as? SpanSizeCallback)?.getSpanSize(spanCount, position, itemCount)
            ?: defaultSpanSize
    }

}

fun ListModel<*>.overrideSpanSize(callback: SpanSizeCallback?) {
    setProperty(KEY_SPAN_SIZE_OVERRIDE, callback, false)
}

fun ListModel<*>.overrideSpanSize(callback: (totalSpanCount: Int, position: Int, itemCount: Int) -> Int) {
    overrideSpanSize(object : SpanSizeCallback {
        override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int =
            callback.invoke(totalSpanCount, position, itemCount)
    })
}