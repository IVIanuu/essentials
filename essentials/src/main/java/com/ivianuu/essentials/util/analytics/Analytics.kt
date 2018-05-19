/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.util.analytics

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.ivianuu.essentials.util.ext.d
import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * Basic analytics logger
 */
object Analytics {

    var logger = FabricAnalyticsLogger()

    fun log(event: String) {
        logger.log(event)
    }

    interface Logger {
        fun log(event: String)
    }
}

/**
 * Logs events via fabric
 */
class FabricAnalyticsLogger : Analytics.Logger {
    override fun log(event: String) {
        if (Fabric.isInitialized()) {
            Answers.getInstance().logCustom(CustomEvent(event))
        } else {
            Timber.tag("Analytics")
            d { event }
        }
    }
}