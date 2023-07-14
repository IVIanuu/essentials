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

  fun logMessage(priority: Priority = DEBUG, tag: String, message: String)

  enum class Priority { VERBOSE, DEBUG, INFO, WARN, ERROR, WTF }
}

inline fun Logger.log(
  priority: Logger.Priority = DEBUG,
  @Inject tag: LoggingTag,
  message: () -> String
) {
  if (isLoggingEnabled.value)
    logMessage(priority, tag.value, message())
}

expect fun Throwable.asLog(): String

object NoopLogger : Logger {
  override val isLoggingEnabled: LoggingEnabled
    get() = LoggingEnabled(false)

  override fun logMessage(priority: Logger.Priority, tag: String, message: String) {
  }
}

@Provide class PrintingLogger(override val isLoggingEnabled: LoggingEnabled) : Logger {
  override fun logMessage(priority: Logger.Priority, tag: String, message: String) {
    println("[${priority.name}] $tag $message")
  }
}

@JvmInline value class LoggingTag(val value: String) {
  @Provide companion object {
    @Provide inline fun loggingTag(sourceKey: SourceKey) = LoggingTag(sourceKey.value)
  }
}

@JvmInline value class LoggingEnabled(val value: Boolean)
