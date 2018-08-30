package com.ivianuu.essentials.sample.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import com.ivianuu.essentials.ui.state.bindStateViewModel
import com.ivianuu.essentials.ui.state.stateEpoxyController
import com.ivianuu.essentials.ui.traveler.detour.FadeDetour
import com.ivianuu.essentials.util.Result
import com.ivianuu.essentials.util.ext.COMPUTATION
import com.uber.autodispose.autoDisposable
import com.uber.autodispose.subscribeBy
import io.reactivex.Single
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

    private val viewModel by bindStateViewModel<ListViewModel, Result<List<String>>>()

    override val toolbarMenuRes = R.menu.fragment_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "List"
    }

    override fun epoxyController() = stateEpoxyController(viewModel) { state ->
        when (state) {
            is Result.Loading -> {
                simpleLoading {
                    id("loading")
                }
            }
            is Result.Success -> {
                if (state.data.isNotEmpty()) {
                    state.data.forEach { title ->
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> viewModel.refreshClicked()
        }
        return true
    }
}

class ListViewModel @Inject constructor() : StateViewModel<Result<List<String>>>() {

    init {
        setInitialState(Result.Loading)
        generateNewState()
    }

    fun refreshClicked() {
        generateNewState()
    }

    private fun generateNewState() {
        Single.just(Unit)
            .doOnSubscribe { setState { Result.Loading } }
            .subscribeOn(COMPUTATION)
            .map { generateList() }
            .delay(1, TimeUnit.SECONDS)
            .autoDisposable(scopeProvider)
            .subscribeBy { setState { Result.Success(it) } }
    }

    private fun generateList() = when (listOf(1, 2, 3).shuffled().first()) {
        1 -> (0..5000).map { "Title: $it" }
        2 -> (0..10).map { "Title: $it" }
        else -> emptyList()
    }
}

@EpoxyModelClass(layout = R.layout.single_line_list_item)
abstract class SingleLineListItemModel : BaseEpoxyModel() {

    @EpoxyAttribute lateinit var title: String

    override fun bind(holder: BaseEpoxyHolder) {
        super.bind(holder)
        holder.title.text = title
    }

}