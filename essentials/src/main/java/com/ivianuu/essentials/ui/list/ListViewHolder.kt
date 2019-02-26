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

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * View holder used by [ListAdapter]s
 */
class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var model: ListModel<ListHolder>? = null
        private set

    lateinit var holder: ListHolder
        private set
    private var holderCreated = false

    fun bind(model: ListModel<*>) {
        this.model = model as ListModel<ListHolder>

        if (!holderCreated) {
            holderCreated = true
            holder = model.createHolder()
            holder.bindView(itemView)
        }

        model.bind(holder)
    }

    fun unbind() {
        model?.unbind(holder)
        model = null
    }

}

fun ListViewHolder.requireModel(): ListModel<ListHolder> = model ?: error("model is null")