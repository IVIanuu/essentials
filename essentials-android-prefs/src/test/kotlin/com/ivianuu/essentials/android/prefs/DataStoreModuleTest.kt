/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.test.*
import io.kotest.matchers.*
import org.junit.*
import java.nio.file.*

class DataStoreModuleTest {
  @Test fun testBasic() = runCancellingBlockingTest {
    val dataStore = prefsDataStoreModule.dataStore(
      dispatcher = dispatcher,
      prefsDir = { Files.createTempDirectory("tmp").toFile() },
      scope = this
    )

    val collector = dataStore.data.testCollect(this)

    collector.values[0] shouldBe emptyMap()

    dataStore.updateData {
      toMutableMap()
        .apply {
          this["a"] = "1"
        }
    }

    collector.values[1] shouldBe mapOf("a" to "1")

    dataStore.updateData {
      toMutableMap()
        .apply {
          this -= "a"
          this["b"] = "2"
        }
    }

    collector.values[2] shouldBe mapOf("b" to "2")
  }
}
