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

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ResultBindingTest {

    @Test
    fun testErrorBinding() {
        var invocations = 0
        val doSomething = {
            invocations++
            Err(Throwable()) as Result<Unit, Throwable>
        }

        binding {
            !doSomething()
            !doSomething()
        }

        expectThat(invocations).isEqualTo(1)
    }

}