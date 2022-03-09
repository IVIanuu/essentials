/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import com.ivianuu.essentials.logging.Logger.Priority.*

interface Logger {
  val isEnabled: LoggingEnabled

  fun log(tag: String, message: String, priority: Priority = DEBUG)

  enum class Priority {
    VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
  }
}

inline fun Logger.log(
  tag: String = loggingTag(),
  priority: Logger.Priority = DEBUG,
  message: () -> String
) {
  if (isEnabled.value) log(tag, message(), priority)
}

expect fun Throwable.asLog(): String

expect inline fun loggingTag(): String

object NoopLogger : Logger {
  override val isEnabled: LoggingEnabled
    get() = LoggingEnabled(false)

  override fun log(tag: String, message: String, priority: Logger.Priority) {
  }
}

class PrintingLogger(override val isEnabled: LoggingEnabled) : Logger {
  override fun log(tag: String, message: String, priority: Logger.Priority) {
    println("[${priority.name}] $tag $message")
  }
}

inline class LoggingEnabled(val value: Boolean)
