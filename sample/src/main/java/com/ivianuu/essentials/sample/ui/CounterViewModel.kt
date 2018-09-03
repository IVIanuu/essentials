package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.ui.state.StateViewModel
import com.ivianuu.traveler.Router
import javax.inject.Inject

/**
 * Counter view model
 */
class CounterViewModel @Inject constructor(private val router: Router) :
    StateViewModel<CounterState>() {

    private lateinit var destination: CounterDestination

    fun setDestination(destination: CounterDestination) {
        if (this::destination.isInitialized) return
        this.destination = destination
        setInitialState(CounterState(destination.screen, 0))
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
        withState { router.navigateTo(CounterDestination(it.screen + 1)) }
    }

    fun screenDownClicked() {
        router.finish()
    }

    fun rootScreenClicked() {
        router.popToRoot()
    }

    fun listScreenClicked() {
        router.navigateTo(ListDestination)
    }
}

data class CounterState(
    val screen: Int,
    val count: Int
)