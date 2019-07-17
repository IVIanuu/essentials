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

package com.ivianuu.essentials.sample.ui.list

import com.ivianuu.essentials.ui.epoxy.ListItem
import com.ivianuu.essentials.ui.epoxy.SimpleLoading
import com.ivianuu.essentials.ui.epoxy.SimpleText
import com.ivianuu.essentials.ui.menu.PopupMenu
import com.ivianuu.essentials.ui.menu.PopupMenuItem
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxEpoxyController
import com.ivianuu.essentials.ui.mvrx.injekt.injectMvRxViewModel
import com.ivianuu.essentials.ui.navigation.director.controllerRoute
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.fade
import com.ivianuu.injekt.Inject

val listRoute = controllerRoute<ListController>(options = controllerRouteOptions().fade())

@Inject
class ListController : com.ivianuu.essentials.ui.simple.ListController() {

    override val toolbarTitle get() = "List"

    override val toolbarMenu: PopupMenu<*>?
        get() = PopupMenu<String>(
            items = listOf(PopupMenuItem(value = "Refresh", title = "Refresh")),
            onSelected = {
                when (it) {
                    "Refresh" -> viewModel.refreshClicked()
                }
            }
        )

    private val viewModel: ListViewModel by injectMvRxViewModel()

    override fun epoxyController() = mvRxEpoxyController(viewModel) { state ->
        when {
            state.loading -> SimpleLoading(id = "loading")
            state.items.isNotEmpty() -> state.items.forEach { ListItem(id = it, title = it) }
            else -> SimpleText(text = "Hmm empty", id = "empty")
        }
    }

}