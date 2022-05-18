/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.collections.set

interface Navigator {
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

suspend fun Navigator.clear() {
  setBackStack(emptyList())
}

@Provide @Scoped<AppScope> class NavigatorImpl(
  private val keyHandlers: List<KeyHandler<*>>,
  rootKey: RootKey? = null,
  private val L: Logger,
  scope: NamedCoroutineScope<AppScope>
) : Navigator {
  val _backStack = MutableStateFlow(listOfNotNull<Key<*>>(rootKey))
  override val backStack: StateFlow<List<Key<*>>> by this::_backStack

  private val _results = EventFlow<Pair<Key<*>, Any?>>()
  override val results: Flow<Pair<Key<*>, Any?>> by this::_results

  private val actor = scope.actor()

  override suspend fun setBackStack(backStack: List<Key<*>>, results: Map<Key<*>, Any?>) {
    backStack.groupBy { it }
      .forEach {
        check(it.value.size == 1) {
          "Back stack cannot contain duplicates ${it.key}"
        }
      }

    actor.act {
      val finalResults = results.toMutableMap()
      _backStack.value = buildList {
        for (key in backStack) {
          @Suppress("UNCHECKED_CAST")
          key as Key<Any?>

          val keyHandle = keyHandlers.firstNotNullOfOrNull { (it as KeyHandler<Any?>)(key) }

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
}
