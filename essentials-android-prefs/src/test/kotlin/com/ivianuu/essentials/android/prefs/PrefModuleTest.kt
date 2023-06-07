/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.data.TestDataStore
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollectIn
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.junit.Test
import java.nio.file.Files

class PrefModuleTest {
  @Test fun testBasic() = runCancellingBlockingTest {
    val prefsDataStore = prefsDataStoreModule.dataStore(
      coroutineContext = dispatcher,
      prefsDir = { Files.createTempDirectory("tmp").toFile() },
      scope = this,
      initial = { emptyMap() }
    )

    val dataStore = PrefModule { MyPrefs() }.dataStore(
      prefsDataStore = prefsDataStore,
      scope = this
    )

    val prefsCollector = prefsDataStore.data.testCollectIn(this)
    val modelCollector = dataStore.data.testCollectIn(this)

    delay(100)

    prefsCollector.values[0] shouldBe emptyMap()
    modelCollector.values[0] shouldBe MyPrefs(0, 0)

    dataStore.updateData { copy(a = a.inc()) }

    prefsCollector.values[1] shouldBe mapOf("a" to "1", "b" to "0")
    modelCollector.values[1] shouldBe MyPrefs(1, 0)

    prefsDataStore.updateData {
      toMutableMap()
        .apply {
          this -= "a"
          this["b"] = "2"
        }
    }

    prefsCollector.values[2] shouldBe mapOf("b" to "2")
    modelCollector.values[2] shouldBe MyPrefs(0, 2)
  }

  @Test fun testNullableFieldWithDefaultValue() = runCancellingBlockingTest {
    val dataStore = PrefModule { NullableWithDefault() }.dataStore(
      prefsDataStore = TestDataStore(emptyMap()),
      scope = this
    )

    dataStore.data.first().string shouldBe "a"
  }

  @Test fun testNullableFieldWithDefaultValueWhichIsNull() = runCancellingBlockingTest {
    val dataStore = PrefModule { NullableWithDefault() }.dataStore(
      prefsDataStore = TestDataStore(emptyMap()),
      scope = this
    )

    dataStore.updateData { copy(null) }

    dataStore.data.first().string shouldBe null
  }

  @Test fun testNullableFieldWithDefaultValue2() = runCancellingBlockingTest {
    val dataStore = PrefModule { NullableWithDefault() }.dataStore(
      prefsDataStore = TestDataStore(emptyMap()),
      scope = this
    )

    dataStore.updateData { copy("a") }

    dataStore.data.first().string shouldBe "a"
  }

  @Serializable data class NullableWithDefault(val string: String? = "a")

  @Serializable data class MyPrefs(val a: Int = 0, val b: Int = 0)
}
