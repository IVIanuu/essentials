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

package com.ivianuu.essentials.ui.viewmodel

import com.ivianuu.essentials.util.SavedState

interface ViewModelListener {
    fun preInitialize(viewModel: ViewModel, savedState: SavedState?) {
    }

    fun postInitialize(viewModel: ViewModel, savedState: SavedState?) {
    }

    fun preDestroy(viewModel: ViewModel) {
    }

    fun postDestroy(viewModel: ViewModel) {
    }

    fun onSaveState(viewModel: ViewModel, savedState: SavedState) {
    }
}

fun ViewModelListener(
    preInitialize: ((viewModel: ViewModel, savedState: SavedState?) -> Unit)? = null,
    postInitialize: ((viewModel: ViewModel, savedState: SavedState?) -> Unit)? = null,
    preDestroy: ((viewModel: ViewModel) -> Unit)? = null,
    postDestroy: ((viewModel: ViewModel) -> Unit)? = null,
    onSaveState: ((viewModel: ViewModel, savedState: SavedState) -> Unit)? = null
): ViewModelListener = LambdaViewModelListener(
    preInitialize, postInitialize, preDestroy, postDestroy, onSaveState
)

fun ViewModel.doOnPreInitialize(
    block: (viewModel: ViewModel, savedState: SavedState?) -> Unit
): ViewModelListener = addViewModelListener(preInitialize = block)

fun ViewModel.doOnPostInitialize(
    block: (viewModel: ViewModel, savedState: SavedState?) -> Unit
): ViewModelListener = addViewModelListener(postInitialize = block)

fun ViewModel.doOnPreDestroy(
    block: (viewModel: ViewModel) -> Unit
): ViewModelListener = addViewModelListener(preDestroy = block)

fun ViewModel.doOnPostDestroy(
    block: (viewModel: ViewModel) -> Unit
): ViewModelListener = addViewModelListener(postDestroy = block)

fun ViewModel.doOnSaveState(
    block: (viewModel: ViewModel, savedState: SavedState) -> Unit
): ViewModelListener = addViewModelListener(onSaveState = block)

fun ViewModel.addViewModelListener(
    preInitialize: ((viewModel: ViewModel, savedState: SavedState?) -> Unit)? = null,
    postInitialize: ((viewModel: ViewModel, savedState: SavedState?) -> Unit)? = null,
    preDestroy: ((viewModel: ViewModel) -> Unit)? = null,
    postDestroy: ((viewModel: ViewModel) -> Unit)? = null,
    onSaveState: ((viewModel: ViewModel, savedState: SavedState) -> Unit)? = null
): ViewModelListener = ViewModelListener(
    preInitialize,
    postInitialize, preDestroy, postDestroy, onSaveState
)
    .also(this::addListener)

class LambdaViewModelListener(
    private val preInitialize: ((viewModel: ViewModel, savedState: SavedState?) -> Unit)? = null,
    private val postInitialize: ((viewModel: ViewModel, savedState: SavedState?) -> Unit)? = null,
    private val preDestroy: ((viewModel: ViewModel) -> Unit)? = null,
    private val postDestroy: ((viewModel: ViewModel) -> Unit)? = null,
    private val onSaveState: ((viewModel: ViewModel, savedState: SavedState) -> Unit)? = null
) : ViewModelListener {
    override fun preInitialize(viewModel: ViewModel, savedState: SavedState?) {
        preInitialize?.invoke(viewModel, savedState)
    }

    override fun postInitialize(viewModel: ViewModel, savedState: SavedState?) {
        postInitialize?.invoke(viewModel, savedState)
    }

    override fun preDestroy(viewModel: ViewModel) {
        preDestroy?.invoke(viewModel)
    }

    override fun postDestroy(viewModel: ViewModel) {
        postDestroy?.invoke(viewModel)
    }

    override fun onSaveState(viewModel: ViewModel, savedState: SavedState) {
        onSaveState?.invoke(viewModel, savedState)
    }
}

internal sealed class ViewModelListenerStore {
    abstract fun getListeners(): List<ViewModelListener>

    object Global : ViewModelListenerStore() {
        override fun getListeners(): List<ViewModelListener> =
            viewModelListeners.toList()
    }

    class Manager(private val manager: ViewModelManager) : ViewModelListenerStore() {
        override fun getListeners(): List<ViewModelListener> =
            Global.getListeners() + manager.viewModelListeners.toList()
    }
}