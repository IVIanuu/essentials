package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.ui.test.TestViewModel
import com.ivianuu.traveler.Router
import javax.inject.Inject

/**
 * Counter view model
 */
class CounterViewModel @Inject constructor(private val router: Router) :
    TestViewModel<CounterState, CounterEvent>() {

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
        withState {
            offerEvent { CounterEvent.NavigateTo(CounterDestination(it.screen + 1)) }
            //router.navigateTo(CounterDestination(it.screen + 1))
        }
    }

    fun screenDownClicked() {
        offerEvent { CounterEvent.GoBack }
        //router.exit()
    }

    fun rootScreenClicked() {
        offerEvent { CounterEvent.GoToRoot }
        //router.backToRoot()
    }

    fun listScreenClicked() {
        offerEvent { CounterEvent.GoToList }
        //router.navigateTo(ListDestination)
    }
}

sealed class CounterEvent {
    data class NavigateTo(val destination: CounterDestination) : CounterEvent()
    object GoBack : CounterEvent()
    object GoToRoot : CounterEvent()
    object GoToList : CounterEvent()
}

data class CounterState(
    val screen: Int,
    val count: Int
)