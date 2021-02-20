/*
 * Copyright 2020 Manuel Wrage
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

import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.AppInitializerBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext

@AppInitializerBinding
@Given
fun workerAppInitializer(
    @Given appContext: AppContext,
    @Given workerFactory: WorkerFactory,
): AppInitializer = {
    WorkManager.initialize(
        appContext,
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    )
}

@Given
fun workManager(@Given appContext: AppContext) =
    WorkManager.getInstance(appContext)
