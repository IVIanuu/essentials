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

/**
 * A listener for [ViewModel]s
 */
interface ViewModelListener {

    fun preInit(viewModel: ViewModel, savedInstanceState: SavedState?) {
    }

    fun postInit(viewModel: ViewModel, savedInstanceState: SavedState?) {
    }

    fun onRestoreInstanceState(viewModel: ViewModel, savedInstanceState: SavedState) {
    }

    fun onSaveInstanceState(viewModel: ViewModel, outState: SavedState) {
    }

    fun preCleared(viewModel: ViewModel) {
    }

    fun postCleared(viewModel: ViewModel) {
    }

}