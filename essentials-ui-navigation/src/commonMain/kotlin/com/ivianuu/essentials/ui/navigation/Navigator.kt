/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Stable
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.collections.set

@Stable interface Navigator {
  val backStack: StateFlow<List<Key<*>>>

  val results: Flow<Pair<Key<*>, Any?>>

  suspend fun setBackStack(backStack: List<Key<*>>, results: Map<Key<*>, Any?> = emptyMap())
}

suspend fun <R> Navigator.awaitResult(key: Key<R>): R? = results
  .firstOrNull { it.first == key }
  ?.second as? R

suspend fun <R> Navigator.setRoot(key: Key<R>): R? {
  setBackStack(listOf(key))
  return awaitResult(key)
}

suspend fun <R> Navigator.push(key: Key<R>): R? {
  setBackStack(backStack.value.filter { it != key } + key)
  return awaitResult(key)
}

suspend fun <R> Navigator.replaceTop(key: Key<R>): R? {
  val newBackStack = backStack.value.toMutableList()
  if (newBackStack.lastOrNull() == key)
    return awaitResult(key)
  newBackStack.removeLast()
  newBackStack += key
  setBackStack(newBackStack)
  return awaitResult(key)
}

suspend fun <R> Navigator.pop(key: Key<R>, result: R? = null) {
  setBackStack(backStack.value.filter { it != key }, mapOf(key to result))
}

suspend fun Navigator.popTop(): Boolean {
  val currentBackStack = backStack.value
  return if (currentBackStack.isNotEmpty()) {
    pop(currentBackStack.last())
    true
  } else false
}

suspend fun Navigator.popTo(key: Key<*>) {
  val index = backStack.value.indexOfLast { it == key }
  check(index != -1) {
    "Key $key was not in ${backStack.value}"
  }
  setBackStack(
    backStack.value
      .take(index + 1)
  )
}

suspend fun Navigator.clear() {
  setBackStack(emptyList())
}

fun Navigator(
  initialBackStack: List<Key<*>> = emptyList(),
  keyInterceptors: List<KeyInterceptor<*>> = emptyList(),
  scope: CoroutineScope
): Navigator = NavigatorImpl(
  initialBackStack = initialBackStack,
  keyInterceptors = keyInterceptors,
  scope = scope
)

class NavigatorImpl(
  initialBackStack: List<Key<*>>,
  private val keyInterceptors: List<KeyInterceptor<*>>,
  scope: CoroutineScope
) : Navigator {
  private val _backStack = MutableStateFlow(initialBackStack)
  override val backStack: StateFlow<List<Key<*>>> by this::_backStack

  private val _results = EventFlow<Pair<Key<*>, Any?>>()
  override val results: Flow<Pair<Key<*>, Any?>> by this::_results

  private val actor = scope.actor()

  override suspend fun setBackStack(backStack: List<Key<*>>, results: Map<Key<*>, Any?>) {
    backStack.groupBy { it }
      .forEach {
        check(it.value.size == 1) {
          "Back stack cannot contain duplicates ${it.key} -> $backStack"
        }
      }

    actor.act {
      val finalResults = results.toMutableMap()
      _backStack.value = buildList {
        for (key in backStack) {
          @Suppress("UNCHECKED_CAST")
          key as Key<Any?>

          val keyHandle =
            keyInterceptors.firstNotNullOfOrNull { it.cast<KeyInterceptor<Any?>>()(key) }

          if (keyHandle == null) {
            add(key)
          } else {
            finalResults[key] = keyHandle()
          }
        }
      }

      finalResults.forEach { _results.emit(it.key to it.value) }
    }
  }

  companion object {
    @Provide fun appNavigator(
      rootKey: RootKey?,
      keyInterceptors: List<KeyInterceptor<*>>,
      scope: ScopedCoroutineScope<AppScope>
    ): @Scoped<AppScope> Navigator = NavigatorImpl(
      initialBackStack = listOfNotNull(rootKey),
      keyInterceptors = keyInterceptors,
      scope = scope
    )
  }
}
