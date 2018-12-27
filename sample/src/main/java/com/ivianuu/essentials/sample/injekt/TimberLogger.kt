package com.ivianuu.essentials.sample.injekt

import com.ivianuu.injekt.InjektPlugins
import com.ivianuu.timberktx.d
import com.ivianuu.timberktx.e
import com.ivianuu.timberktx.i
import com.ivianuu.timberktx.w
import timber.log.Timber

/**
 * Timber logger
 */
class TimberLogger : InjektPlugins.Logger {
    override fun debug(msg: String) {
        Timber.tag(TAG)
        d { msg }
    }

    override fun info(msg: String) {
        Timber.tag(TAG)
        i { msg }
    }

    override fun warn(msg: String) {
        Timber.tag(TAG)
        w { msg }
    }

    override fun error(msg: String, throwable: Throwable?) {
        Timber.tag(TAG)
        e(throwable) { msg }
    }

    private companion object {
        private const val TAG = "Injekt"
    }
}