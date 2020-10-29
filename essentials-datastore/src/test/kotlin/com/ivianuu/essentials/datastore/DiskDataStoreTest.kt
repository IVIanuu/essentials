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

import com.ivianuu.essentials.datastore.disk.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.disk.MoshiSerializerFactory
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.squareup.moshi.Moshi
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import java.nio.file.Files

class DiskDataStoreTest {

    private val storeDir = Files.createTempDirectory("tmp").toFile()
    private val scope = TestCoroutineScope()
    private val factory = DiskDataStoreFactory(
        scope,
        { storeDir },
        { MoshiSerializerFactory(Moshi.Builder().build()) }
    )

    private inline fun <reified T> createStore(name: String, noinline produceDefaultData: () -> T) =
        factory.create(name, produceDefaultData) to storeDir.resolve(name)

    @Test
    fun testWrite() = scope.runCancellingBlockingTest {
        val (store, file) = createStore("test") { 0 }
        store.updateData { 1 }
        store.data.first() shouldBe 1
        file.readText().toInt() shouldBe 1
        store.updateData { it + 1 }
        store.data.first() shouldBe 2
        file.readText().toInt() shouldBe 2
    }

    @Test
    fun testSingleCollector() = scope.runCancellingBlockingTest {
        val (store) = createStore("test") { 0 }

        val datas = mutableListOf<Int>()
        val collectorJob = launch {
            store.data
                .collect { datas += it }
        }

        datas.shouldContainExactly(0)
        store.updateData { it + 1 }
        datas.shouldContainExactly(0, 1)
        store.updateData { it + 1 }
        store.updateData { it + 1 }
        store.updateData { it }
        datas.shouldContainExactly(0, 1, 2, 3)

        collectorJob.cancelAndJoin()
    }

    @Test
    fun testMultipleCollector() = scope.runCancellingBlockingTest {
        val (store) = createStore("test") { 0 }

        val datas1 = mutableListOf<Int>()
        launch {
            store.data
                .collect { datas1 += it }
        }
        val datas2 = mutableListOf<Int>()
        launch {
            store.data
                .collect { datas2 += it }
        }

        datas1.shouldContainExactly(0)
        datas2.shouldContainExactly(0)
        store.updateData { it + 1 }
        datas1.shouldContainExactly(0, 1)
        datas2.shouldContainExactly(0, 1)
        store.updateData { it + 1 }
        store.updateData { it + 1 }
        store.updateData { it }
        datas1.shouldContainExactly(0, 1, 2, 3)
        datas2.shouldContainExactly(0, 1, 2, 3)
    }

}
