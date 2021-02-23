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

import android.util.Log
import com.ivianuu.essentials.util.Logger.Kind
import com.ivianuu.essentials.util.Logger.Kind.DEBUG
import com.ivianuu.essentials.util.Logger.Kind.ERROR
import com.ivianuu.essentials.util.Logger.Kind.INFO
import com.ivianuu.essentials.util.Logger.Kind.VERBOSE
import com.ivianuu.essentials.util.Logger.Kind.WARN
import com.ivianuu.essentials.util.Logger.Kind.WTF
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent

@Given
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

@Scoped<AppComponent>
@Given
fun androidLogger(
    @Given buildInfo: BuildInfo,
    @Given androidLoggerFactory: () -> AndroidLogger,
    @Given noopLoggerFactory: () -> NoopLogger
): Logger = if (buildInfo.isDebug) androidLoggerFactory() else noopLoggerFactory()

@Given
inline val @Given BuildInfo.defaultLoggingEnabled: LoggingEnabled
    get() = isDebug
