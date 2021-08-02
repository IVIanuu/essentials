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

import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface Navigator {
  val state: StateFlow<NavigationState>
  suspend fun <R> push(key: Key<R>): R?
  suspend fun <R> replaceTop(key: Key<R>): R?
  suspend fun <R> pop(key: Key<R>, result: R? = null)
  suspend fun popTop()
}

data class NavigationState(val backStack: List<Key<*>> = emptyList())

@Provide @Scoped<AppScope>
class NavigatorImpl(
  private val intentKeyHandler: IntentKeyHandler,
  private val logger: Logger,
  rootKey: RootKey? = null,
  scope: InjektCoroutineScope<AppScope>
) : Navigator {
  private val _state = MutableStateFlow(State(listOfNotNull(rootKey)))
  override val state: StateFlow<NavigationState>
    get() = _state.mapState { NavigationState(it.backStack) }

  private val actor = scope.actor()

  init {
    if (logger.isEnabled) {
      _state
        .map { it.backStack }
        .distinctUntilChanged()
        .onEach { d { "back stack changed -> $it" } }
        .launchIn(scope)
    }
  }

  override suspend fun <R> push(key: Key<R>): R? {
    val result = CompletableDeferred<R?>()
    actor.act {
      d { "push $key" }
      _state.value.results[key]
        ?.safeAs<CompletableDeferred<Any?>>()
        ?.complete(null)
      if (!intentKeyHandler(key) {
          @Suppress("UNCHECKED_CAST")
          result.complete(it as R)
        }) {
        _state.update {
          copy(
            backStack = backStack
              .filter { it != key } + key,
            results = results + mapOf(key to result)
          )
        }
      }
    }
    return result.await()
  }

  override suspend fun <R> replaceTop(key: Key<R>): R? {
    val result = CompletableDeferred<R?>()
    actor.act {
      d { "replace top $key" }
      _state.value.results[key]
        ?.safeAs<CompletableDeferred<Any?>>()
        ?.complete(null)
      if (intentKeyHandler(key) {
          @Suppress("UNCHECKED_CAST")
          result.complete(it as R)
        }) {
        _state.update {
          copy(
            backStack = backStack.dropLast(1),
            results = results - key
          )
        }
      } else {
        _state.update {
          copy(
            backStack = backStack
              .filter { it != key }
              .dropLast(1) + key,
            results = results + mapOf(key to result)
          )
        }
      }
    }
    return result.await()
  }

  override suspend fun <R> pop(key: Key<R>, result: R?) {
    actor.act {
      d { "pop $key" }
      _state.update { popKey(key, result) }
    }
  }

  override suspend fun popTop() {
    actor.act {
      val topKey = state.first().backStack.last()
      d { "pop top $topKey" }
      _state.update {
        @Suppress("UNCHECKED_CAST")
        popKey(topKey, null)
      }
    }
  }

  data class State(
    val backStack: List<Key<*>> = emptyList(),
    val results: Map<Key<*>, CompletableDeferred<out Any?>> = emptyMap(),
  )

  private fun <R> State.popKey(key: Key<R>, result: R?): State {
    @Suppress("UNCHECKED_CAST")
    val resultAction = results[key] as? CompletableDeferred<R?>
    resultAction?.complete(result)
    return copy(
      backStack = backStack - key,
      results = results - key
    )
  }
}
