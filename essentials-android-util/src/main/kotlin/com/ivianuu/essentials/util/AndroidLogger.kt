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
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

@Binding
class AndroidLogger : Logger {

    override fun v(message: String?, throwable: Throwable?, tag: String?) {
        Log.v(tag ?: stackTraceTag, message, throwable)
    }

    override fun d(message: String?, throwable: Throwable?, tag: String?) {
        Log.d(tag ?: stackTraceTag, message, throwable)
    }

    override fun i(message: String?, throwable: Throwable?, tag: String?) {
        Log.i(tag ?: stackTraceTag, message, throwable)
    }

    override fun w(message: String?, throwable: Throwable?, tag: String?) {
        Log.w(tag ?: stackTraceTag, message, throwable)
    }

    override fun e(message: String?, throwable: Throwable?, tag: String?) {
        Log.e(tag ?: stackTraceTag, message, throwable)
    }

    override fun wtf(message: String?, throwable: Throwable?, tag: String?) {
        Log.wtf(tag ?: stackTraceTag, message, throwable)
    }

    companion object {
        @Binding(ApplicationComponent::class)
        fun binding(
            buildInfo: BuildInfo,
            androidLoggerProvider: () -> AndroidLogger,
            noopLoggerProvider: () -> NoopLogger,
        ): Logger {
            return if (buildInfo.isDebug) androidLoggerProvider() else noopLoggerProvider()
        }
    }
}
