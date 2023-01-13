/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import android.util.Log
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.logging.Logger.Priority
import com.ivianuu.essentials.logging.Logger.Priority.DEBUG
import com.ivianuu.essentials.logging.Logger.Priority.ERROR
import com.ivianuu.essentials.logging.Logger.Priority.INFO
import com.ivianuu.essentials.logging.Logger.Priority.VERBOSE
import com.ivianuu.essentials.logging.Logger.Priority.WARN
import com.ivianuu.essentials.logging.Logger.Priority.WTF
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scope
import kotlin.math.min

context(SystemBuildInfo) @Provide class AndroidLogger(
  override val isLoggingEnabled: LoggingEnabled
) : Logger {
  override fun logMessage(tag: String, message: String, priority: Priority) {
    val trimmedTag = if (tag.length <= MAX_TAG_LENGTH || systemSdk >= 26) {
      tag
    } else {
      tag.substring(0, MAX_TAG_LENGTH)
    }

    var i = 0
    val length = message.length
    while (i < length) {
      var newline = message.indexOf('\n', i)
      newline = if (newline != -1) newline else length
      do {
        val end = min(newline, i + MAX_LOG_LENGTH)
        val part = message.substring(i, end)
        logToLogcat(priority, trimmedTag, part)
        i = end
      } while (i < newline)
      i++
    }
  }

  private fun logToLogcat(priority: Priority, tag: String, part: String) {
    when (priority) {
      VERBOSE -> Log.v(tag, part)
      DEBUG -> Log.d(tag, part)
      INFO -> Log.i(tag, part)
      WARN -> Log.w(tag, part)
      ERROR -> Log.e(tag, part)
      WTF -> Log.wtf(tag, part)
    }
  }

  companion object {
    private const val MAX_LOG_LENGTH = 4000
    private const val MAX_TAG_LENGTH = 23

    @Provide inline fun androidLogger(
      crossinline loggingEnabled: () -> LoggingEnabled,
      crossinline androidLoggerFactory: () -> AndroidLogger,
      scope: Scope<AppScope>
    ): Logger = scope.scoped {
      if (loggingEnabled().value) androidLoggerFactory()
      else NoopLogger
    }
  }
}

context(BuildInfo) @Provide fun androidLoggingEnabled() = LoggingEnabled(isDebug)
