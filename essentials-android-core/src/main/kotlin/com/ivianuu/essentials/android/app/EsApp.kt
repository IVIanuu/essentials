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

package com.ivianuu.essentials.android.app

import android.app.Application
import android.content.pm.ApplicationInfo
import com.ivianuu.essentials.android.data.esData
import com.ivianuu.essentials.android.ui.core.esUiInitializerInjection
import com.ivianuu.essentials.android.util.esAndroidUtil
import com.ivianuu.essentials.app.AppComponentHolder
import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.app.AppInitializers
import com.ivianuu.essentials.app.AppService
import com.ivianuu.essentials.app.AppServices
import com.ivianuu.essentials.app.esAppInitializerInjection
import com.ivianuu.essentials.app.esAppServiceInjection
import com.ivianuu.essentials.moshi.android.esMoshiAndroid
import com.ivianuu.essentials.moshi.esMoshi
import com.ivianuu.essentials.util.ComponentBuilderInterceptor
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.containsFlag
import com.ivianuu.essentials.util.esUtil
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentOwner
import com.ivianuu.injekt.InjektPlugins
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.android.AndroidLogger
import com.ivianuu.injekt.android.ApplicationComponent
import com.ivianuu.injekt.android.systemServices
import com.ivianuu.injekt.get
import com.ivianuu.injekt.getLazy

/**
 * App
 */
abstract class EsApp : Application(), ComponentOwner, ComponentBuilderInterceptor {

    override val component: Component
        get() {
            if (!AppComponentHolder.isInitialized) {
                configureInjekt()
                initializeComponent()
            }

            return AppComponentHolder.component
        }

    private val appServices: Map<String, Provider<AppService>> by getLazy(qualifier = AppServices)

    private val logger: Logger by getLazy()

    override fun onCreate() {
        super.onCreate()
        invokeInitializers()
        startAppServices()
    }

    protected open fun configureInjekt() {
        if (applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
            InjektPlugins.logger = AndroidLogger()
        }
    }

    protected open fun initializeComponent() {
        AppComponentHolder.init(
            ApplicationComponent(this) {
                esAppBindings()
                esAppInitializerInjection()
                esAppServiceInjection()
                esData()
                esMoshi()
                esMoshiAndroid()
                esUiInitializerInjection()
                esUtil()
                esAndroidUtil()
                systemServices()
                buildComponent()
            }
        )
    }

    protected open fun invokeInitializers() {
        get<Map<String, Provider<AppInitializer>>>(qualifier = AppInitializers)
            .forEach {
                logger.d("initialize ${it.key}")
                it.value()
            }
    }

    protected open fun startAppServices() {
        appServices
            .forEach {
                logger.d("start service ${it.key}")
                it.value()
            }
    }
}
