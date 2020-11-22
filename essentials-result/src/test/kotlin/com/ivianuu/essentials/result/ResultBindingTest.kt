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

package com.ivianuu.essentials.result

import io.kotest.matchers.shouldBe
import org.junit.Test

class ResultBindingTest {

    @Test
    fun testErrorBinding() {
        var invocations = 0
        val doSomething: () -> Result<Unit, Throwable> = {
            invocations++
            Err(Throwable())
        }

        binding {
            !doSomething()
            !doSomething()
        }

        invocations shouldBe 1
    }

}