package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.kSerializer
import org.junit.Test

class PrefModuleTest {
  @Test fun testBasic() = runCancellingBlockingTest {
    val prefsDataStore = /*prefsDataStoreModule.dataStore(
      dispatcher = dispatcher,
      prefsDir = { Files.createTempDirectory("tmp").toFile() },
      // todo remove arg once injekt is fixed
      serializerFactory = { kSerializer<Map<String, String>>() },
      scope = this,
      initial = { mapOf("c" to "D") }
    )*/ object : DataStore<Map<String, String>> {
      override val data = MutableStateFlow(mapOf("c" to "D"))
      override suspend fun updateData(transform: Map<String, String>.() -> Map<String, String>): Map<String, String> {
        return transform(data.value)
      }
    }

    val dataStore = PrefModule { MyPrefs() }.dataStore(
      dispatcher = dispatcher,
      prefsDataStore = prefsDataStore,
      // todo remove arg once injekt is fixed
      serializerFactory = { kSerializer() },
      scope = this
    )

    //val prefsCollector = prefsDataStore.data.testCollect(this)
    //val modelCollector = dataStore.data.testCollect(this)

    //prefsCollector.values[0] shouldBe emptyMap()
    //modelCollector.values[0] shouldBe MyPrefs(0, 0)

    dataStore.updateData { copy(a = a.inc()) }

    //prefsCollector.values[1] shouldBe mapOf("a" to 1, "b" to 0)
    //modelCollector.values[1] shouldBe MyPrefs(1, 0)

    prefsDataStore.updateData {
      toMutableMap()
        .apply {
          this -= "a"
          this["b"] = "2"
        }
    }

    //prefsCollector.values[1] shouldBe mapOf("b" to "2")
    //modelCollector.values[1] shouldBe MyPrefs(0, 2)
  }

  @Serializable data class MyPrefs(val a: Int = 0, val b: Int = 0)
}
