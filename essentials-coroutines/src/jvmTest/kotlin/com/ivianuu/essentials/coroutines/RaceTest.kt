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
import io.kotest.matchers.*
import kotlinx.coroutines.*
import org.junit.*

class RaceTest {
  @Test
  fun testFirstOneWins() = runCancellingBlockingTest {
    val result = race {
      launchRacer {
        delay(1)
        "a"
      }
      launchRacer { "b" }
    }
    result shouldBe "b"
  }

  @Test
  fun testFinishedRacerCancelsOtherRacers() = runCancellingBlockingTest {
    var bCancelled = false
    race {
      launchRacer {
        delay(1)
        "a"
      }
      launchRacer {
        try {
          awaitCancellation()
        } catch (e: CancellationException) {
          bCancelled = true
          throw e
        }
      }
    }

    bCancelled shouldBe true
  }

  @Test
  fun testErrorInRacerPropagatesException() = runCancellingBlockingTest {
    var thrown = false
    try {
      race<String> {
        launchRacer {
          throw RuntimeException()
        }
      }
    } catch (e: Throwable) {
      thrown = true
    }

    thrown shouldBe true
  }

  @Test
  fun testFinishedRacerCancelsBlock() = runCancellingBlockingTest {
    var blockCancelled = false
    race {
      launchRacer {
        delay(1)
      }
      try {
        awaitCancellation()
      } catch (e: CancellationException) {
        blockCancelled = true
      }
    }

    blockCancelled shouldBe true
  }
}
