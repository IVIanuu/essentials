/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.util

import android.util.*
import com.ivianuu.essentials.util.Logger.*
import com.ivianuu.essentials.util.Logger.Kind.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*

@Given
@Factory
class AndroidLogger(@Given override val isEnabled: LoggingEnabled) : Logger {
    override fun log(kind: Kind, message: String?, throwable: Throwable?, tag: String?) {
        when (kind) {
            VERBOSE -> Log.v(tag ?: stackTraceTag, message, throwable)
            DEBUG -> Log.d(tag ?: stackTraceTag, message, throwable)
            INFO -> Log.i(tag ?: stackTraceTag, message, throwable)
            WARN -> Log.w(tag ?: stackTraceTag, message, throwable)
            ERROR -> Log.e(tag ?: stackTraceTag, message, throwable)
            WTF -> Log.wtf(tag ?: stackTraceTag, message, throwable)
        }
    }
}

@Given
inline fun androidLogger(
    @Given buildInfo: BuildInfo,
    @Given androidLoggerFactory: () -> @Factory AndroidLogger,
    @Given noopLoggerFactory: () -> @Factory NoopLogger
): @Scoped<AppGivenScope> Logger =
    if (buildInfo.isDebug) androidLoggerFactory() else noopLoggerFactory()

@Given
fun defaultLoggingEnabled(@Given buildInfo: BuildInfo): LoggingEnabled = buildInfo.isDebug
