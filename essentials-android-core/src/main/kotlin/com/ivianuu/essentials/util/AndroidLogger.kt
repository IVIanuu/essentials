package com.ivianuu.essentials.util

import android.util.Log
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

@Given
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
        @Given
        fun bind(): Logger? = if (given<BuildInfo>().isDebug) given<AndroidLogger>() else null
    }
}
