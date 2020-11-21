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

import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.junit.Test

class RaceTest {

    @Test
    fun testRace() = runCancellingBlockingTest {
        var aFinished = false
        var aCancelled = false
        var bFinished = false
        var bCancelled = false
        race<Unit> {
            launchRacer {
                try {
                    delay(1)
                    aFinished = true
                } catch (e: CancellationException) {
                    aCancelled = true
                    throw e
                }
            }
            launchRacer {
                try {
                    bFinished = true
                } catch (e: CancellationException) {
                    bCancelled = true
                    throw e
                }
            }
        }

        aFinished shouldBe false
        aCancelled shouldBe true
        bFinished shouldBe true
        bCancelled shouldBe false
    }

}
