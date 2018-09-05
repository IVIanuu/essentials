package com.ivianuu.essentials.sample.ui

import android.os.Bundle
import android.view.MenuItem
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.ivianuu.compass.Destination
import com.ivianuu.compass.Detour
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.epoxy.BaseEpoxyHolder
import com.ivianuu.essentials.ui.epoxy.BaseEpoxyModel
import com.ivianuu.essentials.ui.epoxy.simpleLoading
import com.ivianuu.essentials.ui.epoxy.simpleText
import com.ivianuu.essentials.ui.simple.SimpleFragment
import com.ivianuu.essentials.ui.state.StateViewModel
import com.ivianuu.essentials.ui.state.bindViewModel
import com.ivianuu.essentials.ui.state.stateEpoxyController
import com.ivianuu.essentials.ui.traveler.detour.FadeDetour
import com.ivianuu.essentials.util.ext.COMPUTATION
import com.ivianuu.essentials.util.ext.andTrue
import com.ivianuu.essentials.util.ext.d
import com.ivianuu.essentials.util.ext.subscribeUi
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.single_line_list_item.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Observable.interval(1, TimeUnit.SECONDS)
            .subscribeUi(this) { d { "sub -> $it" } }
    }

    override fun epoxyController() = stateEpoxyController(viewModel) { state ->
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

class ListViewModel @Inject constructor() : StateViewModel<ListState>() {

    init {
        setInitialState(ListState(false, emptyList()))
        generateNewState()

        subscribe { d { "state changed -> $it" } }
    }

    fun refreshClicked() {
        generateNewState()
    }

    private fun generateNewState() {
        Single.just(Unit)
            .subscribeOn(COMPUTATION)
            .map { generateList() }
            .delay(1, TimeUnit.SECONDS)
            .doOnSubscribe { setState { copy(loading = true) } }
            .doOnSuccess { setState { copy(loading = false) } }
            .subscribeBy { setState { copy(items = it) } }
            .disposeOnClear()
    }

    private fun generateList() = when (listOf(1, 2, 3).shuffled().first()) {
        1 -> (0..500).map { "Title: $it" }
        2 -> (0..20).map { "Title: $it" }
        else -> emptyList()
    }
}

data class ListState(
    val loading: Boolean,
    val items: List<String>
)

@EpoxyModelClass(layout = R.layout.single_line_list_item)
abstract class SingleLineListItemModel : BaseEpoxyModel() {

    @EpoxyAttribute lateinit var title: String

    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)
        holder.title.text = title
    }

}