/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import com.ivianuu.essentials.logging.Logger.Priority.DEBUG
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.SourceKey

interface Logger {
  val isLoggingEnabled: LoggingEnabled

  operator fun invoke(priority: Priority = DEBUG, tag: String, message: String)

  enum class Priority {
    VERBOSE, DEBUG, INFO, WARN, ERROR, WTF
  }
}

@JvmName("logWithTag") inline operator fun Logger.invoke(
  tag: String,
  priority: Logger.Priority = DEBUG,
  message: () -> String
) = this(priority, LoggingTag(tag), message)

inline operator fun Logger.invoke(
  priority: Logger.Priority = DEBUG,
  @Inject tag: LoggingTag,
  message: () -> String
) {
  if (isLoggingEnabled.value)
    this(priority, tag.value, message())
}

expect fun Throwable.asLog(): String

object NoopLogger : Logger {
  override val isLoggingEnabled: LoggingEnabled
    get() = LoggingEnabled(false)

  override fun invoke(priority: Logger.Priority, tag: String, message: String) {
  }
}

@Provide class PrintingLogger(override val isLoggingEnabled: LoggingEnabled) : Logger {
  override fun invoke(priority: Logger.Priority, tag: String, message: String) {
    println("[${priority.name}] $tag $message")
  }
}

inline class LoggingTag(val value: String) {
  companion object {
    @Provide inline fun loggingTag(sourceKey: SourceKey) = LoggingTag(sourceKey.value)
  }
}

inline class LoggingEnabled(val value: Boolean)
