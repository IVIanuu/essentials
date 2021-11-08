/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.data

import com.ivianuu.essentials.InitialOrDefault
import com.ivianuu.essentials.serialization.KTypeT
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import org.junit.Test
import java.io.File
import java.nio.file.Files

class DiskDataStoreTest {
  private val storeDir = Files.createTempDirectory("tmp").toFile()

  private fun <T : Any> CoroutineScope.createStore(
    name: String,
    serializer: Serializer<T>? = null,
    @Inject type: KTypeT<T>,
    @Provide defaultData: @Provide () -> @InitialOrDefault T
  ): Pair<DataStore<T>, File> = DiskDataStore(
    coroutineContext = coroutineContext,
    produceFile = { File(storeDir, name) },
    produceSerializer = serializer?.let { { it } } ?: inject()
  ) to storeDir.resolve(name)

  @Test fun testWrite() = runCancellingBlockingTest {
    val (store, file) = createStore("test") { 0 }
    store.updateData { 1 }
    store.data.first() shouldBe 1
    file.readText().toInt() shouldBe 1
    store.updateData { inc() }
    store.data.first() shouldBe 2
    file.readText().toInt() shouldBe 2
  }

  @Test fun testSingleCollector() = runCancellingBlockingTest {
    val (store) = createStore("test") { 0 }

    val collector = store.data.testCollect(this)

    collector.values.shouldContainExactly(0)
    store.updateData { inc() }
    collector.values.shouldContainExactly(0, 1)
    store.updateData { inc() }
    store.updateData { inc() }
    store.updateData { this }
    collector.values.shouldContainExactly(0, 1, 2, 3)
  }

  @Test fun testMultipleCollector() = runCancellingBlockingTest {
    val (store) = createStore("test") { 0 }

    val collector1 = store.data.testCollect(this)
    val collector2 = store.data.testCollect(this)

    collector1.values.shouldContainExactly(0)
    collector2.values.shouldContainExactly(0)
    store.updateData { inc() }
    collector1.values.shouldContainExactly(0, 1)
    collector2.values.shouldContainExactly(0, 1)
    store.updateData { inc() }
    store.updateData { inc() }
    store.updateData { this }
    collector1.values.shouldContainExactly(0, 1, 2, 3)
    collector2.values.shouldContainExactly(0, 1, 2, 3)
  }

  @Test fun testReadErrorWillBePropagatedToObservers() = runCancellingBlockingTest {
    val (store, file) = createStore("test") { 0 }
    file.writeText("corrupt_data")

    val collector = store.data.testCollect(this)
    collector.error.shouldBeTypeOf<SerializerException>()
  }

  @Test fun testWriteErrorWillBePropagatedToCaller() = runCancellingBlockingTest {
    val (store) = createStore(
      "test",
      serializer = object : Serializer<Int> {
        override val defaultData: Int
          get() = 0

        override fun deserialize(serializedData: String): Int = serializedData.toInt()

        override fun serialize(data: Int): String {
          throw SerializerException("error")
        }
      }
    ) { 0 }

    shouldThrow<RuntimeException> {
      store.updateData { 1 }
    }
  }
}
