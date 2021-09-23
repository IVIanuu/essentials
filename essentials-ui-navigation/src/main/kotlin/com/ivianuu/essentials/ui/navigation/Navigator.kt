/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.coroutines.actor
import com.ivianuu.essentials.coroutines.update2
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.safeAs
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface Navigator {
  val backStack: StateFlow<List<Key<*>>>

  suspend fun <R> push(key: Key<R>): R?

  suspend fun <R> replaceTop(key: Key<R>): R?

  suspend fun <R> pop(key: Key<R>, result: R? = null)

  suspend fun popTop()
}

@Provide @Scoped<AppScope>
class NavigatorImpl(
  private val keyHandlers: Set<KeyHandler<*>> = emptySet(),
  private val logger: Logger,
  rootKey: RootKey? = null,
  scope: NamedCoroutineScope<AppScope>
) : Navigator {
  private val _backStack = MutableStateFlow(listOfNotNull<Key<*>>(rootKey))
  override val backStack: StateFlow<List<Key<*>>> get() = _backStack

  private val results = mutableMapOf<Key<*>, CompletableDeferred<out Any?>>()

  private val actor = scope.actor()

  init {
    if (logger.isEnabled) {
      _backStack
        .onEach { log { "back stack changed -> $it" } }
        .launchIn(scope)
    }
  }

  override suspend fun <R> push(key: Key<R>): R? {
    val result = CompletableDeferred<R?>()

    actor.act {
      log { "push $key" }
      results[key]
        ?.safeAs<CompletableDeferred<Any?>>()
        ?.complete(null)

      @Suppress("UNCHECKED_CAST")
      if (keyHandlers.none { it(key) { result.complete(it as R) } }) {
        _backStack.update2 { filter { it != key } + key }
        results[key] = result
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
      if (keyHandlers.any { it(key) { result.complete(it as R) } }) {
        _backStack.update2 { dropLast(1) }
        results.remove(key)
      } else {
        _backStack.update2 {
          filter { it != key }
            .dropLast(1) + key
        }
        results[key] = result
      }
    }

    return result.await()
  }

  override suspend fun <R> pop(key: Key<R>, result: R?) {
    actor.act {
      log { "pop $key" }
      popKey(key, result)
    }
  }

  override suspend fun popTop() {
    actor.act {
      val topKey = backStack.first().last()
      log { "pop top $topKey" }
      popKey(topKey, null)
    }
  }

  private fun <R> popKey(key: Key<R>, result: R?) {
    @Suppress("UNCHECKED_CAST")
    val resultAction = results[key] as? CompletableDeferred<R?>
    resultAction?.complete(result)

    _backStack.update2 { this - key }
    results.remove(key)
  }
}
