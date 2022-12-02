/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import com.ivianuu.essentials.logging.Logger.Priority.DEBUG
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey

interface Logger {
  val isEnabled: LoggingEnabled

  fun log(message: String, priority: Priority = DEBUG, @Inject tag: LoggingTag)

  enum class Priority {
    VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
  }
}

inline fun log(
  priority: Logger.Priority = DEBUG,
  @Inject tag: LoggingTag = throw AssertionError(),
  @Inject logger: Logger = throw AssertionError(),
  message: () -> String
) {
  if (logger.isEnabled.value) logger.log(message(), priority, tag)
}

expect fun Throwable.asLog(): String

object NoopLogger : Logger {
  override val isEnabled: LoggingEnabled
    get() = LoggingEnabled(false)

  override fun log(message: String, priority: Logger.Priority, @Inject tag: LoggingTag) {
  }
}

@Provide class PrintingLogger(override val isEnabled: LoggingEnabled) : Logger {
  override fun log(message: String, priority: Logger.Priority, @Inject tag: LoggingTag) {
    println("[${priority.name}] $tag $message")
  }
}

inline class LoggingTag(val value: String) {
  companion object {
    @Provide inline fun loggingTag(sourceKey: SourceKey) = LoggingTag(sourceKey.value)
  }
}

inline class LoggingEnabled(val value: Boolean)
