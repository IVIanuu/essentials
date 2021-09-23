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
import com.ivianuu.essentials.logging.Logger.Kind.*
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped

@Provide @Scoped<AppScope> class AndroidLogger(override val isEnabled: LoggingEnabled) : Logger {
  override fun log(kind: Kind, @Inject tag: LoggingTag, message: String) {
    when (kind) {
      VERBOSE -> Log.v(tag, message)
      DEBUG -> Log.d(tag, message)
      INFO -> Log.i(tag, message)
      WARN -> Log.w(tag, message)
      ERROR -> Log.e(tag, message)
      WTF -> Log.wtf(tag, message)
    }
  }

  companion object {
    @Provide inline fun androidLogger(
      loggingEnabled: LoggingEnabled,
      androidLoggerFactory: () -> AndroidLogger
    ): Logger = if (loggingEnabled) androidLoggerFactory()
    else NoopLogger
  }
}

@Provide fun androidLoggingEnabled(buildInfo: BuildInfo): LoggingEnabled = buildInfo.isDebug
