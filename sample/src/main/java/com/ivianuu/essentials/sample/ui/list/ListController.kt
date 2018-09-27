package com.ivianuu.essentials.sample.ui.list

import android.view.MenuItem
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.androidktx.appcompat.widget.textFuture
import com.ivianuu.compass.Destination
import com.ivianuu.compass.Detour
import com.ivianuu.compass.director.ControllerDetour
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.common.VerticalChangeHandler
import com.ivianuu.director.popChangeHandler
import com.ivianuu.director.pushChangeHandler
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.epoxy.BaseEpoxyHolder
import com.ivianuu.essentials.ui.epoxy.SimpleEpoxyModel
import com.ivianuu.essentials.ui.epoxy.simpleLoading
import com.ivianuu.essentials.ui.epoxy.simpleText
import com.ivianuu.essentials.ui.mvrx.bindViewModel
import com.ivianuu.essentials.ui.mvrx.simpleEpoxyController
import com.ivianuu.essentials.ui.simple.SimpleController
import com.ivianuu.essentials.util.ext.andTrue
import dagger.Subcomponent
import dagger.android.AndroidInjector
import kotlinx.android.synthetic.main.single_line_list_item.*

class ListDetour : ControllerDetour<ListDestination> {
    override fun setupTransaction(
        destination: ListDestination,
        data: Any?,
        transaction: RouterTransaction
    ) {
        transaction
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
    }
}

@Detour(ListDetour::class)
@Destination(ListController::class)
object ListDestination

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ListController : SimpleController() {

    private val viewModel by bindViewModel(ListViewModel::class)

    override val toolbarMenuRes = R.menu.fragment_list
    override val toolbarTitle = "List"

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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_refresh -> viewModel.refreshClicked().andTrue()
        else -> false
    }
}

@EpoxyModelClass(layout = R.layout.single_line_list_item)
abstract class SingleLineListItemModel : SimpleEpoxyModel() {

    @EpoxyAttribute lateinit var title: String

    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)
        holder.title.textFuture = title
    }

}

@Subcomponent
interface ListControllerSubcomponent : AndroidInjector<ListController> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<ListController>()
}