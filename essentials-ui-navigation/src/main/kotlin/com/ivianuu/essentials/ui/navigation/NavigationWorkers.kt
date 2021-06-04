package com.ivianuu.essentials.ui.navigation

import androidx.activity.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

/* Pops the top key on back presses */
@Provide fun androidBackPressHandler(
  activity: ComponentActivity,
  navigator: Navigator
): ScopeWorker<UiScope> = {
  navigator.state
    .map { it.backStack.size > 1 }
    .distinctUntilChanged()
    .flatMapLatest { if (it) activity.backPresses() else infiniteEmptyFlow() }
    .collect { navigator.popTop() }
}

private fun OnBackPressedDispatcherOwner.backPresses() = callbackFlow<Unit> {
  val callback = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
      trySend(Unit)
    }
  }
  onBackPressedDispatcher.addCallback(callback)
  awaitClose { callback.remove() }
}

/* Pops to root on activity destroy */
@Provide fun popToRootOnActivityDestroyWorker(navigator: Navigator): ScopeWorker<UiScope> = {
  runOnCancellation {
    navigator.state
      .first()
      .backStack
      .drop(1)
      .forEach { navigator.pop(it) }
  }
}
