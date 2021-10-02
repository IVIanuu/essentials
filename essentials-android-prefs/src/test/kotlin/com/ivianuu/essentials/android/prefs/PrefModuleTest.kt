package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.data.TestDataStore
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.kSerializer
import org.junit.Test
import java.nio.file.Files

class PrefModuleTest {
  @Test fun testBasic() = runCancellingBlockingTest {
    val prefsDataStore = prefsDataStoreModule.dataStore(
      dispatcher = dispatcher,
      prefsDir = { Files.createTempDirectory("tmp").toFile() },
      // todo remove arg once injekt is fixed
      serializerFactory = { kSerializer<Map<String, String?>>() },
      scope = this,
      initial = { emptyMap() }
    )

    val dataStore = PrefModule { MyPrefs() }.dataStore(
      dispatcher = dispatcher,
      prefsDataStore = prefsDataStore,
      // todo remove arg once injekt is fixed
      serializerFactory = { kSerializer() },
      scope = this
    )

    val prefsCollector = prefsDataStore.data.testCollect(this)
    val modelCollector = dataStore.data.testCollect(this)

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
      dispatcher = dispatcher,
      prefsDataStore = TestDataStore(emptyMap()),
      // todo remove arg once injekt is fixed
      serializerFactory = { kSerializer() },
      scope = this
    )

    dataStore.data.first().string shouldBe "a"
  }

  @Test fun testNullableFieldWithDefaultValueWhichIsNull() = runCancellingBlockingTest {
    val dataStore = PrefModule { NullableWithDefault() }.dataStore(
      dispatcher = dispatcher,
      prefsDataStore = TestDataStore(emptyMap()),
      // todo remove arg once injekt is fixed
      serializerFactory = { kSerializer() },
      scope = this
    )

    dataStore.updateData { copy(null) }

    dataStore.data.first().string shouldBe null
  }

  @Serializable data class NullableWithDefault(val string: String? = "a")

  @Serializable data class MyPrefs(val a: Int = 0, val b: Int = 0)
}
