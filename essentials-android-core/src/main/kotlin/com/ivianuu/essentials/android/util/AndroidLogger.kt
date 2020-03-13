package com.ivianuu.essentials.android.util

import android.util.Log
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Factory

@Factory
class AndroidLogger : Logger {
    override fun v(message: String, tag: String) {
        Log.v(tag, message)
    }

    override fun d(message: String, tag: String) {
        Log.d(tag, message)
    }

    override fun i(message: String, tag: String) {
        Log.i(tag, message)
    }

    override fun w(message: String, tag: String) {
        Log.w(tag, message)
    }

    override fun e(message: String?, tag: String) {
        Log.e(tag, message)
    }

    override fun wtf(message: String?, tag: String) {
        Log.wtf(tag, message)
    }
}