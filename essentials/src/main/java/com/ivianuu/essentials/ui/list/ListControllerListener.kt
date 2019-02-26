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

/**
 * @author Manuel Wrage (IVIanuu)
 */
interface ListControllerListener {

    fun onAttachedToRecyclerView(controller: ListController, recyclerView: RecyclerView) {
    }

    fun onDetachedFromRecyclerView(controller: ListController, recyclerView: RecyclerView) {
    }

    fun onInterceptBuildModels(controller: ListController, models: MutableList<ListModel<*>>) {
    }

    fun onModelsBuildResult(controller: ListController, result: DiffResult) {
    }

}

fun ListController.doOnAttachedToRecyclerView(
    block: (controller: ListController, recyclerView: RecyclerView) -> Unit
): ListControllerListener = addListener(onAttachedToRecyclerView = block)

fun ListController.doOnDetachedFromRecyclerView(
    block: (controller: ListController, recyclerView: RecyclerView) -> Unit
): ListControllerListener = addListener(onDetachedFromRecyclerView = block)

fun ListController.doOnInterceptBuildModels(
    block: (controller: ListController, models: MutableList<ListModel<*>>) -> Unit
): ListControllerListener = addListener(onInterceptBuildModels = block)

fun ListController.doOnModelsBuildResult(
    block: (controller: ListController, result: DiffResult) -> Unit
): ListControllerListener = addListener(onModelsBuildResult = block)

fun ListController.addListener(
    onAttachedToRecyclerView: ((controller: ListController, recyclerView: RecyclerView) -> Unit)? = null,
    onDetachedFromRecyclerView: ((controller: ListController, recyclerView: RecyclerView) -> Unit)? = null,
    onInterceptBuildModels: ((controller: ListController, models: MutableList<ListModel<*>>) -> Unit)? = null,
    onModelsBuildResult: ((controller: ListController, result: DiffResult) -> Unit)? = null
): ListControllerListener = ListControllerListener(
    onAttachedToRecyclerView, onDetachedFromRecyclerView,
    onInterceptBuildModels,
    onModelsBuildResult
).also { addListener(it) }

fun ListControllerListener(
    onAttachedToRecyclerView: ((controller: ListController, recyclerView: RecyclerView) -> Unit)? = null,
    onDetachedFromRecyclerView: ((controller: ListController, recyclerView: RecyclerView) -> Unit)? = null,
    onInterceptBuildModels: ((controller: ListController, models: MutableList<ListModel<*>>) -> Unit)? = null,
    onModelsBuildResult: ((controller: ListController, result: DiffResult) -> Unit)? = null
): ListControllerListener = LambdaListControllerListener(
    onAttachedToRecyclerView, onDetachedFromRecyclerView,
    onInterceptBuildModels,
    onModelsBuildResult
)

class LambdaListControllerListener(
    private val onAttachedToRecyclerView: ((controller: ListController, recyclerView: RecyclerView) -> Unit)? = null,
    private val onDetachedFromRecyclerView: ((controller: ListController, recyclerView: RecyclerView) -> Unit)? = null,
    private val onInterceptBuildModels: ((controller: ListController, models: MutableList<ListModel<*>>) -> Unit)? = null,
    private val onModelsBuildResult: ((controller: ListController, result: DiffResult) -> Unit)? = null
) : ListControllerListener {

    override fun onAttachedToRecyclerView(controller: ListController, recyclerView: RecyclerView) {
        onAttachedToRecyclerView?.invoke(controller, recyclerView)
    }

    override fun onDetachedFromRecyclerView(
        controller: ListController,
        recyclerView: RecyclerView
    ) {
        onDetachedFromRecyclerView?.invoke(controller, recyclerView)
    }

    override fun onInterceptBuildModels(
        controller: ListController,
        models: MutableList<ListModel<*>>
    ) {
        onInterceptBuildModels?.invoke(controller, models)
    }

    override fun onModelsBuildResult(controller: ListController, result: DiffResult) {
        onModelsBuildResult?.invoke(controller, result)
    }

}