/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import org.junit.Test

class RaceTest {
  @Test fun testFirstOneWins() = runCancellingBlockingTest {
    val result = race(
      {
        delay(1)
        "a"
      },
      { "b" }
    )
    result shouldBe "b"
  }

  @Test fun testFinishedRacerCancelsOtherRacers() = runCancellingBlockingTest {
    var bCancelled = false
    race(
      {
        delay(1)
        "a"
      },
      {
        try {
          awaitCancellation()
        } catch (e: CancellationException) {
          bCancelled = true
          throw e
        }
      }
    )

    bCancelled shouldBe true
  }

  @Test fun testErrorInRacerPropagatesException() = runCancellingBlockingTest {
    var thrown = false
    try {
      race<String>(
        { throw RuntimeException() }
      )
    } catch (e: Throwable) {
      thrown = true
    }

    thrown shouldBe true
  }
}
