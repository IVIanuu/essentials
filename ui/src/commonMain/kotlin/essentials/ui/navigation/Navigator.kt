/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.navigation

import androidx.compose.runtime.*
import essentials.*
import essentials.app.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

@Stable class Navigator(
  initialBackStack: List<Screen<*>> = emptyList(),
  private val screenInterceptors: List<(Screen<*>) -> ScreenInterceptorResult<*>> = emptyList()
) {
  var backStack by mutableStateOf(initialBackStack)
    private set

  private val results = mutableMapOf<Screen<*>, CancellableContinuation<*>>()

  suspend fun <R> push(screen: Screen<R>): R? {
    screenInterceptors.firstNotNullOfOrNull { it(screen) }
      ?.let { return it.invoke().unsafeCast() }

    return suspendCancellableCoroutine { continuation ->
      continuation.invokeOnCancellation { results.remove(screen) }
      backStack += screen
      results[screen] = continuation
    }
  }

  fun newBackStack(backStack: List<Screen<*>>) {
    this.backStack.reversed().forEach { pop(it) }
    this.backStack = backStack
  }

  fun newRoot(screen: Screen<*>) {
    backStack.reversed().forEach { pop(it) }
    backStack += screen
  }

  fun <R> pop(screen: Screen<R>, result: R? = null) {
    backStack -= screen
    results.remove(screen)
      ?.unsafeCast<CancellableContinuation<R?>>()
      ?.takeIf { it.isActive }
      ?.resume(result)
  }

  fun popTop() {
    if (backStack.size > 1)
      pop(backStack.last(), null)
  }
}

fun <R> popWithResult(result: R? = null, navigator: Navigator = inject, screen: Screen<R> = inject) {
  navigator.pop(screen, result)
}

@Provide object NavigatorProviders {
  @Provide fun rootNavigator(
    rootScreen: RootScreen?,
    screenInterceptors: List<(Screen<*>) -> ScreenInterceptorResult<*>>,
  ): @ScopedService<UiScope> Navigator = Navigator(
    initialBackStack = listOfNotNull(rootScreen),
    screenInterceptors = screenInterceptors
  )
}

fun navigator(x: Navigator = inject): Navigator = x

@Tag annotation class NavGraph<N>

object RootNavGraph

@Tag typealias ScreenInterceptorResult<R> = (suspend () -> R?)?

@Provide val defaultScreenInterceptors
  get() = emptyList<(Screen<*>) -> ScreenInterceptorResult<*>>()
