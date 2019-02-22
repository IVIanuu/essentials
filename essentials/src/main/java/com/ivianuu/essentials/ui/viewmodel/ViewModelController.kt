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

// todo testing artifact

/**
import android.os.Bundle

/**
 * @author Manuel Wrage (IVIanuu)
*/
class ViewModelController(private val viewModel: ViewModel) {

fun initialize(savedState: Bundle?) {
viewModel.initialize(savedState)
}

fun saveState(): Bundle = viewModel.saveState()

fun destroy() {
viewModel.destroy()
}

}

private val controllersByViewModel = mutableMapOf<ViewModel, ViewModelController>()

val ViewModel.controller get() = controllersByViewModel.getOrPut(this) {
ViewModelController(this).also {
addListener(object : ViewModelListener {
override fun postDestroy(viewModel: ViewModel) {
super.postDestroy(viewModel)
controllersByViewModel.remove(viewModel)
}
})
}
}

fun ViewModel.dispatchInitialize(savedState: Bundle?) {
controller.initialize(savedState)
}

fun ViewModel.dispatchSaveState(): Bundle = controller.saveState()

fun ViewModel.dispatchDestroy() {
controller.destroy()
}*/