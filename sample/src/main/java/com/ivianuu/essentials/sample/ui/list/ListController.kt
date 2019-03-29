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

import android.view.MenuItem
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.list.EsListHolder
import com.ivianuu.essentials.ui.list.SimpleListModel
import com.ivianuu.essentials.ui.list.simpleLoading
import com.ivianuu.essentials.ui.list.simpleText
import com.ivianuu.essentials.ui.list.text
import com.ivianuu.essentials.ui.mvrx.epoxy.mvRxModelController
import com.ivianuu.essentials.ui.mvrx.injekt.mvRxViewModel
import com.ivianuu.essentials.ui.simple.SimpleController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.vertical
import com.ivianuu.list.annotations.Model
import com.ivianuu.list.id
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.single_line_list_item.title

@Parcelize
class ListKey : ControllerKey(::ListController, NavOptions().vertical())

class ListController : SimpleController() {

    private val viewModel by mvRxViewModel<ListViewModel>()

    override val toolbarMenuRes get() = R.menu.controller_list
    override val toolbarTitle get() = "List"

    override fun modelController() = mvRxModelController(viewModel) { state ->
        if (state.loading) {
            simpleLoading {
                id("loading")
            }
        } else {
            if (state.items.isNotEmpty()) {
                state.items.forEach { title ->
                    singleLineListItem {
                        id(title)
                        title(title)
                    }
                }
            } else {
                simpleText {
                    id("empty")
                    text("Hmm empty..")
                }
            }
        }
    }

    override fun onToolbarMenuItemClicked(item: MenuItem) = when (item.itemId) {
        R.id.action_refresh -> {
            viewModel.refreshClicked()
            true
        }
        else -> false
    }
}

@Model
class SingleLineListItemModel : SimpleListModel() {

    var title by requiredProperty<String>("title")

    override val layoutRes: Int
        get() = R.layout.single_line_list_item

    override fun bind(holder: EsListHolder) {
        super.bind(holder)
        holder.title.text = title
    }

}