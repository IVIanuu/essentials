package com.ivianuu.essentials.ui.navigation

import androidx.activity.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

/* Pops the top key on back presses */
@Given
fun androidBackPressHandler(
    @Given activity: ComponentActivity,
    @Given navigator: Navigator
): ScopeWorker<UiGivenScope> = {
    navigator.state
        .map { it.backStack.size > 1 }
        .distinctUntilChanged()
        .flatMapLatest { if (it) activity.backPresses() else infiniteEmptyFlow() }
        .collect { navigator.popTop() }
}

private fun OnBackPressedDispatcherOwner.backPresses() = callbackFlow<Unit> {
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            offer(Unit)
        }
    }
    onBackPressedDispatcher.addCallback(callback)
    awaitClose { callback.remove() }
}

/* Pops to root on activity destroy */
@Given
fun popToRootOnActivityDestroyWorker(@Given navigator: Navigator): ScopeWorker<UiGivenScope> = {
    runOnCancellation {
        navigator.state
            .first()
            .backStack
            .drop(1)
            .filterIsInstance<Key<Any>>()
            .forEach { navigator.pop(it) }
    }
}
