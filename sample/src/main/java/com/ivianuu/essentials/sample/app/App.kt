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

import android.util.Log
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.app.BaseApp
import com.ivianuu.injectors.Injector

/**
 * App
 */
class App : BaseApp() {

    override fun applicationInjector(): Injector<out BaseApp> =
        DaggerAppComponent.builder().create(this)

    override fun shouldInitializeAppInitializer(clazz: Class<out AppInitializer>): Boolean {
        return super.shouldInitializeAppInitializer(clazz)
            .also { Log.d("testt", "init $clazz app initializer") }
    }

    override fun shouldStartAppService(clazz: Class<out AppService>): Boolean {
        return super.shouldStartAppService(clazz)
            .also { Log.d("testt", "start $clazz app service") }
    }
}