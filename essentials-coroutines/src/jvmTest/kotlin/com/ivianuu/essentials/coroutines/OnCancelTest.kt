/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.collections.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.*

class OnCancelTest {
  @Test fun testOnCancel() = runCancellingBlockingTest {
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

  @Test fun testOnCancelDoesNotRunIfTheCollectorThrows() = runCancellingBlockingTest {
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
