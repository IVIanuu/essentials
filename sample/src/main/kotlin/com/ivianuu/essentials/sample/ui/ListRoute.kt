/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample.ui

import androidx.lifecycle.viewModelScope
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.epoxy.SimpleLoading
import com.ivianuu.essentials.ui.epoxy.SimpleText
import com.ivianuu.essentials.ui.epoxy.model
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxEpoxyController
import com.ivianuu.essentials.ui.mvrx.injekt.injectMvRxViewModel
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.essentials.ui.popup.PopupMenu
import com.ivianuu.essentials.ui.popup.PopupMenuItem
import com.ivianuu.injekt.Inject
import kotlinx.android.synthetic.main.single_line_list_item.title
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val listRoute = controllerRoute<ListController>(options = controllerRouteOptions().fade())

@Inject
class ListController : com.ivianuu.essentials.ui.simple.ListController() {

    override val toolbarTitle: String
        get() = "List"

    override val toolbarMenu: PopupMenu<*>?
        get() = PopupMenu(
            items = listOf(
                PopupMenuItem(title = "Refresh", onSelected = {
                    viewModel.refreshClicked()
                })
            )
        )

    private val viewModel: ListViewModel by injectMvRxViewModel()

    override fun epoxyController() = mvRxEpoxyController(viewModel) { state ->
        when {
            state.loading -> SimpleLoading(id = "loading")
            state.items.isNotEmpty() -> state.items.forEach { SingleLine(it) }
            else -> SimpleText(text = "Hmm empty", id = "empty")
        }
    }

}

private fun EpoxyController.SingleLine(text: String) = model(
    id = text, layoutRes = R.layout.single_line_list_item,
    bind = { title.text = text }
)

@Inject
class ListViewModel : MvRxViewModel<ListState>(ListState()) {

    init {
        logStateChanges()
        generateNewState()
    }

    fun refreshClicked() {
        generateNewState()
    }

    private fun generateNewState() {
        viewModelScope.launch {
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
    val loading: Boolean = true,
    val items: List<String> = emptyList()
)