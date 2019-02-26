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

import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.util.ext.swap
import java.util.concurrent.Executor

/**
 * List adapter for [ListModel]s
 */
class ListAdapter(
    private val controller: ListController,
    diffingExecutor: Executor
) : RecyclerView.Adapter<ListViewHolder>() {

    private val helper = AsyncListDiffer<ListModel<*>>(
        AdapterListUpdateCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK)
            .setBackgroundThreadExecutor(diffingExecutor)
            .build()
    )

    /**
     * All current models
     */
    val models: List<ListModel<*>> get() = _models
    private val _models get() = helper.currentList

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val model = _models.first { it.viewType == viewType }
        val view = model.buildView(parent)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val model = _models[position]
        holder.bind(model)
    }

    override fun onViewRecycled(holder: ListViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun getItemId(position: Int): Long = _models[position].id

    override fun getItemCount(): Int = _models.size

    override fun getItemViewType(position: Int): Int = _models[position].viewType

    override fun onViewAttachedToWindow(holder: ListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.model!!.attach(holder.holder)
    }

    override fun onViewDetachedFromWindow(holder: ListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.model!!.detach(holder.holder)
    }

    override fun onFailedToRecycleView(holder: ListViewHolder): Boolean =
        holder.model!!.failedToRecycleView(holder.holder)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        controller.attachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        controller.detachedFromRecyclerView(recyclerView)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        require(hasStableIds) { "This implementation relies on stable ids" }
        super.setHasStableIds(hasStableIds)
    }

    fun setModels(models: List<ListModel<*>>) {
        helper.submitList(models)
    }

    private companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ListModel<*>>() {
                override fun areItemsTheSame(
                    oldItem: ListModel<*>,
                    newItem: ListModel<*>
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ListModel<*>,
                    newItem: ListModel<*>
                ): Boolean = oldItem == newItem
            }
    }
}

fun ListAdapter.addModel(model: ListModel<*>) {
    val newModels = models.toMutableList()
    newModels.add(model)
    setModels(newModels)
}

fun ListAdapter.removeModel(model: ListModel<*>) {
    val newModels = models.toMutableList()
    newModels.remove(model)
    setModels(newModels)
}

fun ListAdapter.swapModels(from: Int, to: Int) {
    val newModels = models.toMutableList()
    newModels.swap(from, to)
    setModels(newModels)
}

fun ListAdapter.clearModels() {
    setModels(emptyList())
}