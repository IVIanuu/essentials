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

package com.ivianuu.essentials.datastore

import com.squareup.moshi.Moshi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo
import java.nio.file.Files

class DiskDataStoreTest {

    private val storeDir = Files.createTempDirectory("tmp").toFile()
    private val scope = TestCoroutineScope()
    private val factory = DiskDataStoreFactory(
        scope,
        { storeDir },
        MoshiSerializerFactory(Moshi.Builder().build())
    )

    private inline fun <reified T> createStore(name: String, noinline produceDefaultData: () -> T) =
        factory.create(name, produceDefaultData) to storeDir.resolve(name)

    @Test
    fun testWrite() = scope.runBlockingTest {
        val (store, file) = createStore("test") { 0 }
        store.updateData { 1 }
        expectThat(store.data.first()).isEqualTo(1)
        expectThat(file.readText().toInt()).isEqualTo(1)
        store.updateData { it + 1 }
        expectThat(store.data.first()).isEqualTo(2)
        expectThat(file.readText().toInt()).isEqualTo(2)
    }

    @Test
    fun testSingleCollector() = scope.runBlockingTest {
        val (store) = createStore("test") { 0 }

        val datas = mutableListOf<Int>()
        val collectorJob = launch {
            store.data
                .collect { datas += it }
        }

        expectThat(datas).containsExactly(0)
        store.updateData { it + 1 }
        expectThat(datas).containsExactly(0, 1)
        store.updateData { it + 1 }
        store.updateData { it + 1 }
        store.updateData { it }
        expectThat(datas).containsExactly(0, 1, 2, 3)

        collectorJob.cancelAndJoin()
    }

    @Test
    fun testMultipleCollector() = scope.runBlockingTest {
        val (store) = createStore("test") { 0 }

        val datas1 = mutableListOf<Int>()
        val collectorJob1 = launch {
            store.data
                .collect { datas1 += it }
        }
        val datas2 = mutableListOf<Int>()
        val collectorJob2 = launch {
            store.data
                .collect { datas2 += it }
        }

        expectThat(datas1).containsExactly(0)
        expectThat(datas2).containsExactly(0)
        store.updateData { it + 1 }
        expectThat(datas1).containsExactly(0, 1)
        expectThat(datas2).containsExactly(0, 1)
        store.updateData { it + 1 }
        store.updateData { it + 1 }
        store.updateData { it }
        expectThat(datas1).containsExactly(0, 1, 2, 3)
        expectThat(datas2).containsExactly(0, 1, 2, 3)

        collectorJob1.cancelAndJoin()
        collectorJob2.cancelAndJoin()
    }

}
