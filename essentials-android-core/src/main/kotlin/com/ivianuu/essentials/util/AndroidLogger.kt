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
        ): Logger? {
            return if (buildInfo.isDebug) androidLoggerProvider() else null
        }
    }
}
