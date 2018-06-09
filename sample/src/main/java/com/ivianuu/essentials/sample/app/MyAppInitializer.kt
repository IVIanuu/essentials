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

package com.ivianuu.essentials.sample.app

import android.app.Application
import android.util.Log
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.injection.AppInitializerModule
import com.ivianuu.essentials.injection.AppServiceModule
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
@AppInitializerModule
@AutoBindsIntoSet(AppInitializer::class)
class MyAppInitializer @Inject constructor() : AppInitializer {
    override fun init(app: Application) {
        Log.d("testt", "on init")
    }
}

@AppServiceModule
@AutoBindsIntoSet(AppService::class)
class MyAppService @Inject constructor() : AppService {
    override fun start() {
        Log.d("testtt", "start")
    }
}