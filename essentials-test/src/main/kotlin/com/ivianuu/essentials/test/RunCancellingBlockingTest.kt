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

package com.ivianuu.essentials.test

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncompletedCoroutinesError
import kotlinx.coroutines.test.runBlockingTest

fun runCancellingBlockingTest(
    context: CoroutineContext = EmptyCoroutineContext,
    testBody: suspend TestCoroutineScope.() -> Unit
) {
    var testsPassed = false
    try {
        runBlockingTest(context) {
            testBody(this)
            testsPassed = true
            cancel()
        }
    } catch (e: UncompletedCoroutinesError) {
        if (testsPassed) {
            // we are okay - this is a workaround for https://github.com/Kotlin/kotlinx.coroutines/issues/1531
        } else {
            throw e
        }
    } catch (e: CancellationException) {
        // ok job got cancelled
    }
}

fun TestCoroutineScope.runCancellingBlockingTest(
    testBody: suspend TestCoroutineScope.() -> Unit
) {
    var testsPassed = false
    try {
        runBlockingTest {
            testBody(this)
            testsPassed = true
            cancel()
        }
    } catch (e: UncompletedCoroutinesError) {
        if (testsPassed) {
            // we are okay - this is a workaround for https://github.com/Kotlin/kotlinx.coroutines/issues/1531
        } else {
            throw e
        }
    } catch (e: CancellationException) {
        // ok job got cancelled
    }
}
