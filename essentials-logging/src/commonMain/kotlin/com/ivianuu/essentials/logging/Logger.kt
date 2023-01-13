/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import com.ivianuu.essentials.logging.Logger.Priority.DEBUG
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey
import com.ivianuu.injekt.inject

interface Logger {
  val isLoggingEnabled: LoggingEnabled

  fun logMessage(tag: String, message: String, priority: Priority = DEBUG)

  enum class Priority {
    VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
  }
}

context(Logger) @JvmName("logWithTag") inline fun log(
  tag: String,
  priority: Logger.Priority = DEBUG,
  message: () -> String
) {
  with(LoggingTag(tag)) { log(priority, message) }
}

context(Logger, LoggingTag) inline fun log(
  priority: Logger.Priority = DEBUG,
  message: () -> String
) {
  if (isLoggingEnabled.value) logMessage(inject<LoggingTag>().value, message(), priority)
}

expect fun Throwable.asLog(): String

object NoopLogger : Logger {
  override val isLoggingEnabled: LoggingEnabled
    get() = LoggingEnabled(false)

  override fun logMessage(tag: String, message: String, priority: Logger.Priority) {
  }
}

@Provide class PrintingLogger(override val isLoggingEnabled: LoggingEnabled) : Logger {
  override fun logMessage(tag: String, message: String, priority: Logger.Priority) {
    println("[${priority.name}] $tag $message")
  }
}

inline class LoggingTag(val value: String) {
  companion object {
    @Provide inline fun loggingTag(sourceKey: SourceKey) = LoggingTag(sourceKey.value)
  }
}

inline class LoggingEnabled(val value: Boolean)
