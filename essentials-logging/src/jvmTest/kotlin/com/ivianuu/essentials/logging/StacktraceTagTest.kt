package com.ivianuu.essentials.logging

import io.kotest.matchers.*
import org.junit.*

class StacktraceTagTest {
  private class TestLogger : Logger {
    override val isEnabled: Boolean
      get() = true

    var lastTag: String? = null

    override fun log(kind: Logger.Kind, message: String?, throwable: Throwable?, tag: String?) {
      lastTag = stackTraceTag
    }
  }

  @Test
  fun testLoggingTag() {
    val logger = TestLogger()
    logger.d { "msg" }
    logger.lastTag shouldBe "StacktraceTagTest"
  }
}
