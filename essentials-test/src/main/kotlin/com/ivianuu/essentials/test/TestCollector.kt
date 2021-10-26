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

package com.ivianuu.essentials.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

class TestCollector<T>(
  private val onEach: suspend (T) -> Unit = {}
) : FlowCollector<T> {
  val values = mutableListOf<T>()
  var error: Throwable? = null
  override suspend fun emit(value: T) {
    values += value
    onEach(value)
  }
}

fun <T> Flow<T>.testCollect(scope: CoroutineScope, onEach: suspend (T) -> Unit = {}) =
  TestCollector(onEach).also { collector ->
    scope.launch {
      try {
        collect(collector)
      } catch (e: Throwable) {
        collector.error = e
        throw e
      }
    }
  }
