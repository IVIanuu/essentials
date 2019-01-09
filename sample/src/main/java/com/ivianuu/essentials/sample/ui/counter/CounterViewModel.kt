package com.ivianuu.essentials.sample.ui.counter

import android.content.Context
import com.ivianuu.essentials.sample.ui.list.ListKey
import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.horizontal
import com.ivianuu.essentials.util.ext.toast
import com.ivianuu.injekt.annotations.Factory
import com.ivianuu.timberktx.d
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.popToRoot

/**
 * Counter view model
 */
@Factory
class CounterViewModel(
    key: CounterKey,
    private val context: Context,
    private val router: Router
) : MvRxViewModel<CounterState>(CounterState(key.screen)) {

    override fun onCleared() {
        super.onCleared()
        d { "on cleared" }
    }

    fun increaseClicked() {
        setState { copy(count = count.inc()) }
    }

    fun decreaseClicked() {
        setState {
            if (count > 0) {
                copy(count = count.dec())
            } else {
                copy(count = 0)
            }
        }
    }

    fun resetClicked() {
        setState { copy(count = 0) }
    }

    fun screenUpClicked() {
        d { "screen up clicked" }
        withState {
            d { "navigate" }
            router.navigate(
                CounterKey(it.screen.inc()),
                NavOptions().horizontal()
            )
        }
    }

    fun screenDownClicked() {
        router.goBack()
    }

    fun rootScreenClicked() {
        router.popToRoot()
    }

    fun listScreenClicked() {
        router.navigate(ListKey())
    }

    fun doWorkClicked() {
        context.toast("Not implemented")
    }
}

data class CounterState(
    val screen: Int,
    val count: Int = 0
) : MvRxState