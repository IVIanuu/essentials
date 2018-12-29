package com.ivianuu.essentials.app

import android.app.Application
import android.content.pm.ApplicationInfo
import com.ivianuu.essentials.util.ext.containsFlag
import timber.log.Timber

/**
 * Initializes timber in debug builds
 */
class TimberAppInitializer : AppInitializer {

    override fun initialize(app: Application) {
        val isDebuggable = app.applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)
        if (isDebuggable) {
            Timber.plant(Timber.DebugTree())
        }
    }

}