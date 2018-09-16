package com.ivianuu.essentials.util.analytics

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.ivianuu.timberktx.d
import timber.log.Timber
import javax.inject.Inject

/**
 * Logs events via fabric
 */
class FabricAnalyticsLogger @Inject constructor() : Analytics.Logger {
    override fun log(event: String) {
        Answers.getInstance()
            .logCustom(CustomEvent(event))
    }
}

/**
 * Logs events via debugger
 */
class DebugAnalyticsLogger @Inject constructor() : Analytics.Logger {
    override fun log(event: String) {
        Timber.tag("Analytics")
        d { event }
    }
}