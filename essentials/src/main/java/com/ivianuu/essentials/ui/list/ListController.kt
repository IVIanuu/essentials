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

import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor

private val delayedModelBuildHandler = Handler()

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
        cancelPendingModelBuild()

        isBuildingModels = true
        buildModels()
        interceptBuildModels()
        isBuildingModels = false

        adapter.setModels(currentModels.toList())
        currentModels.clear()

        hasBuiltModelsEver = true
    }

    private val delayedModelBuildAction: () -> Unit = {
        buildingExecutor.execute(buildModelsAction)
    }

    private var requestedModelBuildType = RequestedModelBuildType.NONE

    internal val modelListeners get() = _modelListeners
    private val _modelListeners = mutableSetOf<ListModelListener>()

    private val listeners = mutableSetOf<ListControllerListener>()

    open fun requestModelBuild() {
        check(!isBuildingModels) { "cannot call requestModelBuild() inside buildModels()" }
        if (hasBuiltModelsEver) {
            delayedModelBuildHandler.post(delayedModelBuildAction)
        } else {
            buildModelsAction()
        }
    }

    open fun requestImmediateModelBuild() {
        check(!isBuildingModels) { "cannot call requestImmediateModelBuild() inside buildModels()" }
        buildModelsAction()
    }

    open fun requestDelayedModelBuild(delayMs: Long): Unit = synchronized(this) {
        check(!isBuildingModels) {
            "Cannot call requestDelayedModelBuild() from inside buildModels"
        }

        if (requestedModelBuildType == RequestedModelBuildType.DELAYED) {
            cancelPendingModelBuild()
        } else if (requestedModelBuildType == RequestedModelBuildType.NEXT_FRAME) {
            return
        }

        requestedModelBuildType =
            if (delayMs == 0L) RequestedModelBuildType.NEXT_FRAME else RequestedModelBuildType.DELAYED

        delayedModelBuildHandler.postDelayed(delayedModelBuildAction, delayMs)
    }

    fun cancelPendingModelBuild(): Unit = synchronized(this) {
        if (requestedModelBuildType != RequestedModelBuildType.NONE) {
            requestedModelBuildType = RequestedModelBuildType.NONE
            delayedModelBuildHandler.removeCallbacks(delayedModelBuildAction)
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

    protected open fun onInterceptBuildModels(models: MutableList<ListModel<*>>) {
    }

    protected open fun onModelsBuildResult(result: DiffResult) {
    }

    protected open fun onRestoreState(savedState: Bundle) {
    }

    protected open fun onSaveState(savedState: Bundle) {
    }

    fun saveState(): Bundle {
        val bundle = Bundle()

        bundle.putBundle(KEY_ADAPTER_STATE, adapter.saveState())

        val instanceState = Bundle()
        onSaveState(instanceState)
        bundle.putBundle(KEY_INSTANCE_STATE, instanceState)

        return bundle
    }

    fun restoreState(savedState: Bundle?) {
        if (savedState == null) return
        adapter.restoreState(savedState.getBundle(KEY_ADAPTER_STATE))
        onRestoreState(savedState.getBundle(KEY_INSTANCE_STATE)!!)
    }

    @PublishedApi
    internal fun addInternal(model: ListModel<*>) {
        add(model)
    }

    internal fun attachedToRecyclerView(recyclerView: RecyclerView) {
        onAttachedToRecyclerView(recyclerView)
        notifyListeners { it.onAttachedToRecyclerView(this, recyclerView) }
    }

    internal fun detachedFromRecyclerView(recyclerView: RecyclerView) {
        onDetachedFromRecyclerView(recyclerView)
        notifyListeners { it.onDetachedFromRecyclerView(this, recyclerView) }
    }

    internal fun modelsBuildResult(result: DiffResult) {
        onModelsBuildResult(result)
        notifyListeners { it.onModelsBuildResult(this, result) }
    }

    private fun interceptBuildModels() {
        onInterceptBuildModels(currentModels)
        notifyListeners { it.onInterceptBuildModels(this, currentModels) }
    }

    fun addModelListener(listener: ListModelListener) {
        _modelListeners.add(listener)
    }

    fun removeModelListener(listener: ListModelListener) {
        _modelListeners.remove(listener)
    }

    fun addListener(listener: ListControllerListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ListControllerListener) {
        listeners.remove(listener)
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

    private inline fun notifyListeners(block: (ListControllerListener) -> Unit) {
        listeners.toList().forEach(block)
    }

    private enum class RequestedModelBuildType {
        NONE, NEXT_FRAME, DELAYED
    }

    private companion object {
        private const val KEY_ADAPTER_STATE = "ListController.adapter"
        private const val KEY_INSTANCE_STATE = "ListController.instanceState"
    }

}