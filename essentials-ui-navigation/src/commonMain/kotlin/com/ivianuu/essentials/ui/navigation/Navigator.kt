/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.flow.*
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
  if (currentBackStack.isNotEmpty())
    setBackStack(currentBackStack.dropLast(1))
  return currentBackStack.isNotEmpty()
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
          "Back stack cannot contain duplicates ${it.key.}"
        }
      }

    actor.act {
      val finalResults = results.toMutableMap()
      _backStack.value = buildList {
        for (key in backStack) {
          @Suppress("UNCHECKED_CAST")
          key as Key<Any?>
          if (keyHandlers.none {
              (it as KeyHandler<Any?>)(key) {
                finalResults[key] = it
              }
          }) {
            add(key)
          }
        }
      }

      finalResults.forEach { _results.emit(it.key to it.value) }
    }
  }
}
