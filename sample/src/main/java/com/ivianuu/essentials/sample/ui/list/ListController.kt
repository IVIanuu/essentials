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

import android.view.MenuItem
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.epoxy.SimpleLoading
import com.ivianuu.essentials.ui.epoxy.SimpleText
import com.ivianuu.essentials.ui.epoxy.material.ListItem
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxEpoxyController
import com.ivianuu.essentials.ui.mvrx.injekt.injectMvRxViewModel
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.horizontal
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.andTrue
import kotlinx.android.parcel.Parcelize

@Parcelize
object ListKey : ControllerKey(::ListController, NavOptions().horizontal())

class ListController : com.ivianuu.essentials.ui.simple.ListController() {

    private val viewModel: ListViewModel by injectMvRxViewModel()

    override val toolbarMenuRes get() = R.menu.controller_list
    override val toolbarTitle get() = "List"

    override fun epoxyController() = mvRxEpoxyController(viewModel) { state ->
        when {
            state.loading -> SimpleLoading(id = "loading")
            state.items.isNotEmpty() -> state.items.forEach { ListItem(id = it, title = it) }
            else -> SimpleText(text = "Hmm empty", id = "empty")
        }
    }

    override fun onToolbarMenuItemClicked(item: MenuItem) = when (item.itemId) {
        R.id.refresh -> viewModel.refreshClicked().andTrue()
        else -> false
    }
}