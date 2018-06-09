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
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.essentials.injection.EssentialsAppInitializerModule
import com.ivianuu.essentials.util.EnsureMainThreadScheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import javax.inject.Inject

/**
 * Sets our own rxjava main thread scheduler
 */
@EssentialsAppInitializerModule
@AutoBindsIntoSet(AppInitializer::class)
class RxJavaMainThreadAppInitializer @Inject constructor() : AppInitializer {

    override fun init(app: Application) {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { EnsureMainThreadScheduler.INSTANCE }
    }

}