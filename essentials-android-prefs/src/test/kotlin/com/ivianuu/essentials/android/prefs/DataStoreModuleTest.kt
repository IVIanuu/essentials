/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android.prefs

import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.test.testCollect
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.nio.file.Files

class DataStoreModuleTest {
  @Test fun testBasic() = runCancellingBlockingTest {
    val dataStore = DataStoreModule<Map<String, String>>(
      name = "prefs",
      default = { emptyMap() }
    ).dataStore(
      context = dispatcher,
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
