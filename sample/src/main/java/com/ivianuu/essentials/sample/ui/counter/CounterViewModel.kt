package com.ivianuu.essentials.sample.ui.counter

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.State
import androidx.work.WorkManager
import com.ivianuu.assistedinject.Assisted
import com.ivianuu.assistedinject.AssistedInject
import com.ivianuu.essentials.sample.data.MyWorker
import com.ivianuu.essentials.sample.ui.list.ListDestination
import com.ivianuu.essentials.ui.mvrx.MvRxState
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.ext.toObservable
import com.ivianuu.scopes.rx.disposeBy
import com.ivianuu.traveler.Router
import com.ivianuu.traveler.goBack
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.popToRoot

/**
 * Counter view model
 */
class CounterViewModel @AssistedInject constructor(
    @Assisted private val destination: CounterDestination,
    private val router: Router,
    private val toaster: Toaster,
    private val workManager: WorkManager
) : MvRxViewModel<CounterState>(CounterState(screen = destination.screen)) {

    init {
        logStateChanges()
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
        withState { router.navigate(CounterDestination(it.screen.inc())) }
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

    fun doWorkClicked() {
        val request = OneTimeWorkRequestBuilder<MyWorker>().build()
        workManager.enqueue(request)

        workManager.getStatusById(request.id)
            .toObservable()
            .map { it.state }
            .filter { it == State.SUCCEEDED }
            .subscribe { toaster.success("Work finished!") }
            .disposeBy(scope)
    }

    fun backClicked() {
        withState {
            if (it.count > 0) {
                setState { copy(count = count.dec()) }
            } else {
                setState { copy(ignoreBack = true) }
            }
        }
    }
}

data class CounterState(
    val screen: Int = 0,
    val count: Int = 0,
    val ignoreBack: Boolean = false
) : MvRxState