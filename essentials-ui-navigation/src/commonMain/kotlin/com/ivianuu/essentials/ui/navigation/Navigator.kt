/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.safeAs
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.CompletableDeferred

interface Navigator {
  val backStack: List<Key<*>>

  suspend fun <R> setRoot(key: Key<R>): R?

  suspend fun <R> push(key: Key<R>): R?

  suspend fun <R> replaceTop(key: Key<R>): R?

  suspend fun <R> pop(key: Key<R>, result: R? = null)

  suspend fun popTop()

  suspend fun clear()
}

@Provide @Scoped<AppScope> class NavigatorImpl(
  private val keyHandlers: List<KeyHandler<*>>,
  rootKey: RootKey? = null,
  private val L: Logger,
  S: NamedCoroutineScope<AppScope>
) : Navigator {
  override var backStack by mutableStateOf(listOfNotNull<Key<*>>(rootKey))
    private set

  private val results = mutableMapOf<Key<*>, CompletableDeferred<Any?>>()

  private val actor = actor()

  override suspend fun <R> setRoot(key: Key<R>): R? {
    val result = CompletableDeferred<R?>()

    actor.act {
      log { "set root $key" }

      results.forEach { it.value.complete(null) }
      results.clear()

      @Suppress("UNCHECKED_CAST")
      if (keyHandlers.none { (it as KeyHandler<R>).invoke(key) { result.complete(it as R) } }) {
        backStack = listOf(key)
        results[key] = result.cast()
      }
    }

    return result.await()
  }

  override suspend fun <R> push(key: Key<R>): R? {
    val result = CompletableDeferred<R?>()

    actor.act {
      log { "push $key" }
      results[key]
        ?.safeAs<CompletableDeferred<Any?>>()
        ?.complete(null)

      @Suppress("UNCHECKED_CAST")
      if (keyHandlers.none { (it as KeyHandler<R>).invoke(key) { result.complete(it as R) } }) {
        backStack = backStack
          .filter { it != key }
          .plus(key)
        results[key] = result.cast()
      }
    }

    return result.await()
  }

  override suspend fun <R> replaceTop(key: Key<R>): R? {
    val result = CompletableDeferred<R?>()

    actor.act {
      log { "replace top $key" }

      results[key]
        ?.safeAs<CompletableDeferred<Any?>>()
        ?.complete(null)

      @Suppress("UNCHECKED_CAST")
      if (keyHandlers.any { (it as KeyHandler<R>).invoke(key) { result.complete(it as R) } }) {
        backStack = backStack.dropLast(1)
        results.remove(key)
      } else {
        backStack = backStack
          .dropLast(1)
          .filter { it != key }
          .plus(key)
        results[key] = result.cast()
      }
    }

    return result.await()
  }

  override suspend fun <R> pop(key: Key<R>, result: R?) = actor.act {
    log { "pop $key" }
    popKey(key, result)
  }

  override suspend fun popTop() = actor.act {
    val topKey = backStack.last()
    log { "pop top $topKey" }
    popKey(topKey, null)
  }

  override suspend fun clear() = actor.act {
    log { "clear" }
    results.forEach { it.value.complete(null) }
    results.clear()
    backStack = emptyList()
  }

  private fun <R> popKey(key: Key<R>, result: R?) {
    @Suppress("UNCHECKED_CAST")
    val resultAction = results[key] as? CompletableDeferred<R?>
    resultAction?.complete(result)
    backStack -= key
    results.remove(key)
  }
}
