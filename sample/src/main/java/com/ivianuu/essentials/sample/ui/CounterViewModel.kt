package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.ui.state.StateViewModel
import com.ivianuu.essentials.util.lifecycle.LiveEvent
import com.ivianuu.essentials.util.lifecycle.mutableLiveEvent
import com.ivianuu.traveler.Router
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Counter view model
 */
class CounterViewModel @Inject constructor(private val router: Router) :
    StateViewModel<CounterState>() {

    val showToast: LiveEvent<Long> get() = _showToast
    private val _showToast = mutableLiveEvent<Long>()

    private lateinit var destination: CounterDestination

    init {
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribe { _showToast.offer(it) }
            .disposeOnClear()
    }

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
        router.exit()
    }

    fun rootScreenClicked() {
        router.backToRoot()
    }

    fun listScreenClicked() {
        router.navigateTo(ListDestination)
    }
}

data class CounterState(
    val screen: Int,
    val count: Int
)