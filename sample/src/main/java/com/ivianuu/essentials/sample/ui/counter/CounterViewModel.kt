package com.ivianuu.essentials.sample.ui.counter

import com.ivianuu.essentials.sample.ui.list.ListDestination
import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.popToRoot
import javax.inject.Inject

/**
 * Counter view model
 */
class CounterViewModel @Inject constructor(private val router: Router) :
    MvRxViewModel<CounterState>(CounterState()) {

    private lateinit var destination: CounterDestination

    init {
        logStateChanges()
    }

    fun setDestination(destination: CounterDestination) {
        if (this::destination.isInitialized) return
        this.destination = destination
        setState { copy(screen = destination.screen) }
    }

    fun increaseClicked() {
        setState { copy(count = count + 1) }
    }

    fun decreaseClicked() {
        setState {
            if (count > 0) {
                copy(count = count - 1)
            } else {
                copy(count = 0)
            }
        }
    }

    fun resetClicked() {
        setState { copy(count = 0) }
    }

    fun screenUpClicked() {
        withState { router.navigate(CounterDestination(it.screen + 1)) }
    }

    fun screenDownClicked() {
        router.goBack()
    }

    fun rootScreenClicked() {
        router.popToRoot()
    }

    fun listScreenClicked() {
        router.navigate(ListDestination)
    }

}

data class CounterState(
    val screen: Int = 0,
    val count: Int = 0
) : MvRxState