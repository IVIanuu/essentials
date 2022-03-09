/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import io.kotest.matchers.*
import org.junit.*

class LoggerTest {
  @Test fun testLoggingTag() {
    loggingTag() shouldBe "LoggerTest.kt:15"
  }
}
