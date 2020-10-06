/*
 * Copyright 2019 Manuel Wrage
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
import com.ivianuu.essentials.app.AppInitializerBinding
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.MergeInto

@AppInitializerBinding
@FunBinding
fun initializeWorkers(
    applicationContext: ApplicationContext,
    workerFactory: WorkerFactory,
) {
    WorkManager.initialize(
        applicationContext,
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    )
}

@MergeInto(ApplicationComponent::class)
@Module
object EsWorkModule {

    @Binding
    fun workManager(applicationContext: ApplicationContext) =
        WorkManager.getInstance(applicationContext)

}
