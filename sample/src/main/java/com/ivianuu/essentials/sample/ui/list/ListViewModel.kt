package com.ivianuu.essentials.sample.ui.list

import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.coroutines.AppCoroutineDispatchers
import com.ivianuu.scopes.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

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
        scope.launch(dispatchers.computation) {
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
    val loading: Boolean = false,
    val items: List<String> = emptyList()
) : MvRxState
