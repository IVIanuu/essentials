/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.collections.set

class Navigator(
  private val scope: CoroutineScope,
  initialBackStack: List<Screen<*>> = emptyList(),
  private val screenInterceptors: List<ScreenInterceptor<*>> = emptyList(),
) {
  var backStack by mutableStateOf(initialBackStack)
    private set

  private val _results = EventFlow<Pair<Screen<*>, Any?>>()
  val results: Flow<Pair<Screen<*>, Any?>> by this::_results

  private val mutex = Mutex()

  suspend fun setBackStack(backStack: List<Screen<*>>, results: Map<Screen<*>, Any?> = emptyMap()) {
    backStack.groupBy { it }
      .forEach {
        check(it.value.size == 1) {
          "Back stack cannot contain duplicates ${it.key} -> $backStack"
        }
      }

    scope.launch(start = CoroutineStart.UNDISPATCHED) {
      mutex.withLock {
        val finalResults = results.toMutableMap()
        this@Navigator.backStack = buildList {
          for (screen in backStack) {
            @Suppress("UNCHECKED_CAST")
            screen as Screen<Any?>

            val interceptedHandle = screenInterceptors.firstNotNullOfOrNull {
              it.cast<ScreenInterceptor<Any?>>().intercept(screen)
            }

            if (interceptedHandle == null) add(screen)
            else finalResults[screen] = interceptedHandle()
          }
        }

        finalResults.forEach { _results.emit(it.key to it.value) }
      }
    }
  }

  @Provide companion object {
    @Provide fun rootNavigator(
      scope: ScopedCoroutineScope<UiScope>,
      rootScreen: RootScreen?,
      screenInterceptors: List<ScreenInterceptor<*>>,
    ): @Scoped<UiScope> @Service<UiScope> Navigator = Navigator(
      scope = scope,
      initialBackStack = listOfNotNull(rootScreen),
      screenInterceptors = screenInterceptors
    )
  }
}

suspend fun <R> Navigator.awaitResult(screen: Screen<R>): R? = results
  .firstOrNull { it.first == screen }
  ?.second as? R

suspend fun <R> Navigator.setRoot(screen: Screen<R>): R? {
  setBackStack(listOf(screen))
  return awaitResult(screen)
}

suspend fun <R> Navigator.push(screen: Screen<R>): R? {
  setBackStack(backStack.filter { it != screen } + screen)
  return awaitResult(screen)
}

suspend fun <R> Navigator.replaceTop(screen: Screen<R>): R? {
  val currentBackStack = backStack
  setBackStack(
    currentBackStack
      .dropLast(1) + screen
  )
  return awaitResult(screen)
}

suspend fun <R> Navigator.pop(screen: Screen<R>, result: R? = null) {
  setBackStack(backStack.filter { it != screen }, mapOf(screen to result))
}

suspend fun Navigator.popTop(): Boolean {
  val currentBackStack = backStack
  return if (currentBackStack.isNotEmpty()) {
    pop(currentBackStack.last())
    true
  } else false
}

suspend fun Navigator.popTo(screen: Screen<*>) {
  val index = backStack.indexOfLast { it == screen }
  check(index != -1) {
    "Screen $screen was not in $backStack"
  }
  setBackStack(backStack.take(index + 1))
}

suspend fun Navigator.clear() {
  setBackStack(emptyList())
}

val Scope<*>.navigator: Navigator get() = service()

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class NavGraph<N>

object RootNavGraph

fun interface ScreenInterceptor<R> {
  suspend fun intercept(screen: Screen<R>): (suspend () -> R?)?

  @Provide companion object {
    @Provide val defaultScreenInterceptors get() = emptyList<ScreenInterceptor<*>>()
  }
}
