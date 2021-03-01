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

package com.ivianuu.essentials.util

import io.kotest.matchers.collections.shouldContainInOrder
import org.junit.Test

class SortedTopologicalTest {

    @Test
    fun testSortedTopologial() {
        val unsorted = listOf(
            Item(
                key = "c",
                dependencies = setOf("b")
            ),
            Item(
                key = "a",
                dependents = setOf("b")
            ),
            Item(key = "b")
        )

        val sorted = unsorted.sortedTopological(
            key = { it.key },
            dependencies = { it.dependencies },
            dependents = { it.dependents }
        )

        sorted
            .map { it.key }
            .shouldContainInOrder("a", "b", "c")
    }

    private data class Item(
        val key: String,
        val dependencies: Set<String> = emptySet(),
        val dependents: Set<String> = emptySet()
    )

}
