/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import android.util.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.Logger.*
import com.ivianuu.essentials.logging.Logger.Priority.*
import com.ivianuu.injekt.*
import kotlin.math.*

@Provide class AndroidLogger(
  override val isEnabled: LoggingEnabled,
  private val systemBuildInfo: SystemBuildInfo
) : Logger {
  override fun log(priority: Priority, @Inject tag: LoggingTag, message: String) {
    val trimmedTag = if (tag.value.length <= MAX_TAG_LENGTH || systemBuildInfo.sdk >= 26) {
      tag.value
    } else {
      tag.value.substring(0, MAX_TAG_LENGTH)
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
      loggingEnabled: LoggingEnabled,
      androidLoggerFactory: () -> AndroidLogger
    ): Logger = if (loggingEnabled.value) androidLoggerFactory()
    else NoopLogger
  }
}

@Provide fun androidLoggingEnabled(buildInfo: BuildInfo) = LoggingEnabled(buildInfo.isDebug)

