package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.ui.state.StateViewModel
import com.ivianuu.essentials.util.ext.d
import javax.inject.Inject

/**
 * Counter view model
 */
class CounterViewModel @Inject constructor() : StateViewModel<Int>(0) {

    init {
        subscribe { d { "state changed -> $it" } }
    }

    fun increaseClicked() {
        setState { this + 1 }
    }

    fun decreaseClicked() {
        setState {
            if (this > 0) {
                this - 1
            } else {
                0
            }
        }
    }

    fun resetClicked() {
        setState { 0 }
    }
}