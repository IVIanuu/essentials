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

package com.ivianuu.essentials.sample.ui.list

import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.SavedState
import com.ivianuu.essentials.util.coroutineScope
import com.ivianuu.essentials.util.ext.coroutinesDefault
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListViewModel : MvRxViewModel<ListState>(ListState()) {

    init {
        logStateChanges()
    }

    override fun onInitialize(savedState: SavedState?) {
        super.onInitialize(savedState)
        if (savedState == null) {
            generateNewState()
        } else {
            savedState.get<List<String>>("items")?.let {
                setState { copy(items = it) }
            }
        }
    }

    override fun onSaveState(savedState: SavedState) {
        super.onSaveState(savedState)
        val state = peekState()
        if (!state.loading) {
            savedState["items"] = state.items
        }
    }

    fun refreshClicked() {
        generateNewState()
    }

    private fun generateNewState() {
        coroutineScope.launch(coroutinesDefault) {
            setState { copy(loading = true) }
            delay(1000)
            val list = generateList()
            setState { copy(loading = false, items = list) }
        }
    }

    private fun generateList() = when (listOf(1, 2, 3).random()) {
        1 -> (0..500).map { "Title: $it" }
        2 -> (0..20).map { "Title: $it" }
        else -> emptyList()
    }
}

data class ListState(
    val loading: Boolean = false,
    val items: List<String> = emptyList()
)