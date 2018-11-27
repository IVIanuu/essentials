package com.ivianuu.essentials.sample.ui.list

import android.view.MenuItem
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.epoxy.BaseEpoxyHolder
import com.ivianuu.essentials.ui.epoxy.SimpleEpoxyModel
import com.ivianuu.essentials.ui.epoxy.simpleLoading
import com.ivianuu.essentials.ui.epoxy.simpleText
import com.ivianuu.essentials.ui.mvrx.bindViewModel
import com.ivianuu.essentials.ui.mvrx.simpleEpoxyController
import com.ivianuu.essentials.ui.simple.SimpleController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.vertical
import com.ivianuu.essentials.util.ext.andTrue
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.single_line_list_item.title
import javax.inject.Inject
import javax.inject.Provider

@Parcelize
class ListKey : ControllerKey(
    ListController::class,
    NavOptions().vertical()
)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ListController : SimpleController() {

    @Inject lateinit var viewModelProvider: Provider<ListViewModel>
    private val viewModel by bindViewModel { viewModelProvider.get() }

    override val toolbarMenuRes get() = R.menu.controller_list
    override val toolbarTitle get() = "List"

    override fun epoxyController() = simpleEpoxyController(viewModel) { state ->
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
        R.id.action_refresh -> viewModel.refreshClicked().andTrue()
        else -> false
    }
}

@EpoxyModelClass(layout = R.layout.single_line_list_item)
abstract class SingleLineListItemModel : SimpleEpoxyModel() {

    @EpoxyAttribute lateinit var title: String

    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)
        holder.title.text = title
    }

}