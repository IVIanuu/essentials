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

package com.ivianuu.essentials.work

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.ivianuu.essentials.app.AppInitializer
import javax.inject.Inject

/**
 * Initializes the [WorkManager]
 */
class WorkAppInitializer @Inject constructor(
    private val factory: DaggerWorkerFactory
) : AppInitializer {
    override fun initialize(app: Application) {
        WorkManager.initialize(
            app,
            Configuration.Builder()
                .setWorkerFactory(factory)
                .build()
        )
    }
}