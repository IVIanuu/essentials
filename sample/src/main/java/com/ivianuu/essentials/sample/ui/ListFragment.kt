package com.ivianuu.essentials.sample.ui

import android.view.MenuItem
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.compass.Destination
import com.ivianuu.compass.Detour
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.epoxy.BaseEpoxyHolder
import com.ivianuu.essentials.ui.epoxy.SimpleEpoxyModel
import com.ivianuu.essentials.ui.epoxy.simpleLoading
import com.ivianuu.essentials.ui.epoxy.simpleText
import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.mvrx.bindViewModel
import com.ivianuu.essentials.ui.mvrx.simpleEpoxyController
import com.ivianuu.essentials.ui.simple.SimpleFragment
import com.ivianuu.essentials.ui.traveler.detour.FadeDetour
import com.ivianuu.essentials.util.coroutines.AppCoroutineDispatchers
import com.ivianuu.essentials.util.ext.andTrue
import com.ivianuu.essentials.util.ext.setTextFuture
import kotlinx.android.synthetic.main.single_line_list_item.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Detour(FadeDetour::class)
@Destination(ListFragment::class)
object ListDestination

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ListFragment : SimpleFragment() {

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

class ListViewModel @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers
) : MvRxViewModel<ListState>(ListState()) {

    init {
        generateNewState()
    }

    fun refreshClicked() {
        generateNewState()
    }

    private fun generateNewState() {
        launch(dispatchers.computation) {
            setState { copy(loading = true) }
            delay(1, TimeUnit.SECONDS)
            val list = generateList()
            setState { copy(loading = false, items = list) }
        }
    }

    private fun generateList() = when (listOf(1, 2, 3).shuffled().first()) {
        1 -> (0..500).map { "Title: $it" }
        2 -> (0..20).map { "Title: $it" }
        else -> emptyList()
    }
}

data class ListState(
    val loading: Boolean = false,
    val items: List<String> = emptyList()
) : MvRxState

@EpoxyModelClass(layout = R.layout.single_line_list_item)
abstract class SingleLineListItemModel : SimpleEpoxyModel() {

    @EpoxyAttribute lateinit var title: String

    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)
        holder.title.setTextFuture(title)
    }

}