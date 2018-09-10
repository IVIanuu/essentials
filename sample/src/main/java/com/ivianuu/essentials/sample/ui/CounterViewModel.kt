package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.sample.data.ThemeManager
import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.lifecycle.LiveEvent
import com.ivianuu.essentials.util.lifecycle.mutableLiveEvent
import com.ivianuu.traveler.Router
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Counter view model
 */
class CounterViewModel @Inject constructor(
    private val router: Router,
    private val themeManager: ThemeManager
) :
    MvRxViewModel<CounterState>(CounterState()) {

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

    fun toggleThemeClicked() {
        themeManager.toggleTheme()
    }
}

data class CounterState(
    val screen: Int = 0,
    val count: Int = 0
) : MvRxState