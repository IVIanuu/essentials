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
import com.ivianuu.essentials.util.ext.containsFlag
import com.ivianuu.injekt.annotations.Factory
import timber.log.Timber

/**
 * Initializes timber in debug builds
 */
@Factory
class TimberAppInitializer(
    private val app: Application
) : AppInitializer {

    override fun initialize() {
        val isDebuggable = app.applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)
        if (isDebuggable) {
            Timber.plant(Timber.DebugTree())
        }
    }

}