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

import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class ListController(
    private val diffingExecutor: Executor = ListPlugins.defaultDiffingExecutor,
    private val buildingExecutor: Executor = ListPlugins.defaultBuildingExecutor
) {

    val adapter = ListAdapter(this, diffingExecutor)

    var isBuildingModels = false
        private set

    private val currentModels = mutableListOf<ListModel<*>>()

    private var hasBuiltModelsEver = false

    private val buildModelsAction: () -> Unit = {
        currentModels.clear()
        isBuildingModels = true
        buildModels()
        isBuildingModels = false
        adapter.setModels(currentModels.toList())
        currentModels.clear()
        hasBuiltModelsEver = true
    }

    internal val modelListeners get() = _modelListeners
    private val _modelListeners = mutableSetOf<ListModelListener>()

    open fun requestModelBuild() {
        check(!isBuildingModels) { "cannot call requestModelBuild() inside buildModels()" }
        if (hasBuiltModelsEver) {
            buildingExecutor.execute(buildModelsAction)
        } else {
            buildModelsAction()
        }
    }

    protected abstract fun buildModels()

    protected fun add(models: Iterable<ListModel<*>>) {
        checkBuildingModels()
        models.forEach { it.addedToController(this) }
        currentModels.addAll(models)
    }

    protected fun add(vararg models: ListModel<*>) {
        add(models.asIterable())
    }

    protected open fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    }

    protected open fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    }

    @PublishedApi
    internal fun addInternal(model: ListModel<*>) {
        add(model)
    }

    internal fun attachedToRecyclerView(recyclerView: RecyclerView) {
        onAttachedToRecyclerView(recyclerView)
    }

    internal fun detachedFromRecyclerView(recyclerView: RecyclerView) {
        onDetachedFromRecyclerView(recyclerView)
    }

    fun addModelListener(listener: ListModelListener) {
        _modelListeners.add(listener)
    }

    fun removeModelListener(listener: ListModelListener) {
        _modelListeners.remove(listener)
    }

    inline fun <T : ListModel<*>> T.add(block: T.() -> Unit = {}) {
        addTo(this@ListController, block)
    }

    inline operator fun <T : ListModel<*>> ListModelCreator<T>.invoke(block: T.() -> Unit) {
        invoke(this@ListController, block)
    }

    private fun checkBuildingModels() {
        check(isBuildingModels) { "cannot add models outside of buildModels()" }
    }

}