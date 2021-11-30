/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.essentials.InitialOrDefault
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
import kotlin.reflect.KTypeT

class DiskDataStoreTest {
  private val storeDir = Files.createTempDirectory("tmp").toFile()

  private fun <T : Any> createStore(
    name: String,
    serializer: Serializer<T>? = null,
    @Inject type: KTypeT<T>,
    scope: CoroutineScope,
    @Provide defaultData: @Provide () -> @InitialOrDefault T
  ): Pair<DataStore<T>, File> = DiskDataStore(
    coroutineContext = scope.coroutineContext,
    produceFile = { File(storeDir, name) },
    produceSerializer = serializer?.let { { it } } ?: inject()
  ) to storeDir.resolve(name)

  @Test fun testWrite() = runCancellingBlockingTest {
    val (store, file) = createStore("jvmTest") { 0 }
    store.updateData { 1 }
    store.data.first() shouldBe 1
    file.readText().toInt() shouldBe 1
    store.updateData { inc() }
    store.data.first() shouldBe 2
    file.readText().toInt() shouldBe 2
  }

  @Test fun testSingleCollector() = runCancellingBlockingTest {
    val (store) = createStore("jvmTest") { 0 }

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
    val (store) = createStore("jvmTest") { 0 }

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
    val (store, file) = createStore("jvmTest") { 0 }
    file.writeText("corrupt_data")

    val collector = store.data.testCollect(this)
    collector.error.shouldBeTypeOf<SerializerException>()
  }

  @Test fun testWriteErrorWillBePropagatedToCaller() = runCancellingBlockingTest {
    val (store) = createStore(
      "jvmTest",
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
