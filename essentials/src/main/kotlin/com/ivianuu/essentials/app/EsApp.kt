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

package com.ivianuu.essentials.app

import android.app.Application
import android.content.pm.ApplicationInfo
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.injection.EsModule
import com.ivianuu.essentials.util.containsFlag
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.CodegenJustInTimeLookupFactory
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektPlugins
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.android.AndroidLogger
import com.ivianuu.injekt.android.ApplicationComponent
import com.ivianuu.injekt.get
import com.ivianuu.injekt.inject

/**
 * App
 */
abstract class EsApp : Application(), InjektTrait {

    override val component by unsafeLazy {
        configureInjekt()
        createComponent()
    }

    private val appServices: Map<String, Provider<AppService>> by inject(name = AppServices)

    override fun onCreate() {
        super.onCreate()
        invokeInitializers()
        startAppServices()
    }

    protected open fun configureInjekt() {
        if (applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
            InjektPlugins.logger = AndroidLogger()
        } else {
            InjektPlugins.justInTimeLookupFactory = CodegenJustInTimeLookupFactory
        }
    }

    protected open fun createComponent(): Component {
        return ApplicationComponent {
            modules(EsAppModule, EsModule)
            modules(this@EsApp.modules())
        }
    }

    protected open fun invokeInitializers() {
        get<Map<String, Provider<AppInitializer>>>(name = AppInitializers)
            .forEach {
                if (applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
                    println("initialize ${it.key}")
                }
                it.value()
            }
    }

    protected open fun startAppServices() {
        appServices
            .forEach {
                d { "start service ${it.key}" }
                it.value()
            }
    }

    protected open fun modules(): List<Module> = emptyList()
}
