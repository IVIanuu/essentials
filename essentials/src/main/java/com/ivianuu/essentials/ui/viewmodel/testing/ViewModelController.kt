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

package com.ivianuu.essentials.ui.viewmodel.testing

// todo testing artifact


import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.ViewModelListenerStore
import com.ivianuu.essentials.ui.viewmodel.doOnPostDestroy
import com.ivianuu.essentials.util.SavedState

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ViewModelController(private val viewModel: ViewModel) {

    fun initialize(savedState: SavedState?) {
        viewModel.initialize(ViewModelListenerStore.Global, savedState)
    }

    fun restoreState(savedState: SavedState) {
        viewModel.restoreState(savedState)
    }

    fun saveState(): SavedState = viewModel.saveState()

    fun destroy() {
        viewModel.destroy()
    }

}

private val controllersByViewModel = mutableMapOf<ViewModel, ViewModelController>()

val ViewModel.controller: ViewModelController
    get() = controllersByViewModel.getOrPut(this) {
        ViewModelController(this).also {
            doOnPostDestroy { controllersByViewModel.remove(this) }
        }
    }

fun ViewModel.initialize(savedState: SavedState?) {
    controller.initialize(savedState)
}

fun ViewModel.restoreState(savedState: SavedState) {
    controller.restoreState(savedState)
}

fun ViewModel.saveState(): SavedState = controller.saveState()

fun ViewModel.destroy() {
    controller.destroy()
}