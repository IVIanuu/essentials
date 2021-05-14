/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.*
import io.kotest.matchers.collections.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.*

class OnCancelTest {
  @Test
  fun testOnCancel() = runCancellingBlockingTest {
    val sender = EventFlow<String>()
    val received = mutableListOf<String>()
    val job = launch {
      sender
        .onCancel { emit("b") }
        .collect { received += it }
    }

    sender.emit("a")
    job.cancel()

    received.shouldContainExactly("a", "b")
  }

  @Test
  fun testOnCancelDoesNotRunIfTheCollectorThrows() = runCancellingBlockingTest {
    val sender = EventFlow<String>()
    val received = mutableListOf<String>()
    val job = launch {
      sender
        .onCancel { emit("b") }
        .collect {
          received += it
          throw CancellationException()
        }
    }

    sender.emit("a")
    job.cancel()

    received.shouldContainExactly("a")
  }
}
