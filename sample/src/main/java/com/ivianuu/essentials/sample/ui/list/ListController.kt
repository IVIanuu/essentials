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
import com.ivianuu.essentials.ui.traveler.anim.VerticalControllerKeySetup
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.andTrue
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.single_line_list_item.*

@Parcelize
class ListKey : ControllerKey(
    ListController::class,
    VerticalControllerKeySetup()
)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ListController : SimpleController() {

    private val viewModel by bindViewModel(ListViewModel::class)

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