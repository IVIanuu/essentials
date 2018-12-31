package com.ivianuu.essentials.sample.ui.list

import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.ext.coroutinesDefault
import com.ivianuu.injekt.codegen.Factory
import com.ivianuu.timberktx.d
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Factory
class ListViewModel(
    private val listKey: ListKey
) : MvRxViewModel<ListState>(ListState()) {

    init {
        d { "list key $listKey" }
        logStateChanges()
        generateNewState()
    }

    fun refreshClicked() {
        generateNewState()
    }

    private fun generateNewState() {
        coroutineScope.launch(coroutinesDefault) {
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