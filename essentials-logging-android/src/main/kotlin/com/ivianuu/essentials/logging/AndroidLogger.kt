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
import com.ivianuu.essentials.logging.Logger.Kind
import com.ivianuu.essentials.logging.Logger.Kind.DEBUG
import com.ivianuu.essentials.logging.Logger.Kind.ERROR
import com.ivianuu.essentials.logging.Logger.Kind.INFO
import com.ivianuu.essentials.logging.Logger.Kind.VERBOSE
import com.ivianuu.essentials.logging.Logger.Kind.WARN
import com.ivianuu.essentials.logging.Logger.Kind.WTF
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped

@Provide @Scoped<AppScope> class AndroidLogger(override val isEnabled: LoggingEnabled) : Logger {
  override fun log(kind: Kind, tag: LoggingTag, message: String?, throwable: Throwable?) {
    when (kind) {
      VERBOSE -> Log.v(tag, message, throwable)
      DEBUG -> Log.d(tag, message, throwable)
      INFO -> Log.i(tag, message, throwable)
      WARN -> Log.w(tag, message, throwable)
      ERROR -> Log.e(tag, message, throwable)
      WTF -> Log.wtf(tag, message, throwable)
    }
  }

  companion object {
    @Provide inline fun androidLogger(
      loggingEnabled: LoggingEnabled,
      androidLoggerFactory: () -> AndroidLogger,
      noopLoggerFactory: () -> NoopLogger
    ): Logger = if (loggingEnabled) androidLoggerFactory()
    else noopLoggerFactory()
  }
}

@Provide fun androidLoggingEnabled(buildInfo: BuildInfo): LoggingEnabled = buildInfo.isDebug
