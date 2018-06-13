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

package com.ivianuu.essentials.app

import android.app.Application
import android.content.pm.ApplicationInfo
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.essentials.injection.EssentialsAppInitializerModule
import com.ivianuu.essentials.util.analytics.Analytics
import com.ivianuu.essentials.util.analytics.DebugAnalyticsLogger
import com.ivianuu.essentials.util.ext.containsFlag
import timber.log.Timber
import javax.inject.Inject

/**
 * Plants a timber tree while in debug mode
 */
@EssentialsAppInitializerModule
@AutoBindsIntoSet(AppInitializer::class)
class TimberAppInitializer @Inject constructor(
    private val analyticsLogger: DebugAnalyticsLogger
) : AppInitializer {

    override fun init(app: Application) {
        if (app.applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
            Timber.plant(Timber.DebugTree())
            Analytics.addLogger(analyticsLogger)
        }
    }

}