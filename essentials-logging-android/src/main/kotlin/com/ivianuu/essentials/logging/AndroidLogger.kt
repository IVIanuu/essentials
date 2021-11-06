/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.logging

import android.util.Log
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.logging.Logger.Priority
import com.ivianuu.essentials.logging.Logger.Priority.DEBUG
import com.ivianuu.essentials.logging.Logger.Priority.ERROR
import com.ivianuu.essentials.logging.Logger.Priority.INFO
import com.ivianuu.essentials.logging.Logger.Priority.VERBOSE
import com.ivianuu.essentials.logging.Logger.Priority.WARN
import com.ivianuu.essentials.logging.Logger.Priority.WTF
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlin.math.min

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

@Provide fun androidLoggingEnabled(buildInfo: BuildInfo): LoggingEnabled =
  LoggingEnabled(buildInfo.isDebug)

