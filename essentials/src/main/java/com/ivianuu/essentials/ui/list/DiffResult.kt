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

package com.ivianuu.essentials.ui.list

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

data class DiffResult(
    val previousModels: List<ListModel<*>>,
    val newModels: List<ListModel<*>>,
    val differResult: DiffUtil.DiffResult?
) {

    fun dispatchTo(adapter: RecyclerView.Adapter<*>) {
        dispatchTo(AdapterListUpdateCallback(adapter))
    }

    fun dispatchTo(callback: ListUpdateCallback) {
        if (differResult != null) {
            differResult.dispatchUpdatesTo(callback)
        } else if (newModels.isEmpty() && !previousModels.isEmpty()) {
            callback.onRemoved(0, previousModels.size)
        } else if (!newModels.isEmpty() && previousModels.isEmpty()) {
            callback.onInserted(0, newModels.size)
        }
    }
}