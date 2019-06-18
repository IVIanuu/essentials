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
import com.ivianuu.essentials.injection.esModule
import com.ivianuu.essentials.util.ext.containsFlag
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.AndroidLogger
import com.ivianuu.injekt.android.applicationComponent
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner
import kotlin.reflect.KClass

/**
 * App
 */
abstract class EsApp : Application(), InjektTrait, ScopeOwner {

    override val component by unsafeLazy {
        configureInjekt()
        createComponent()
    }

    override val scope: Scope get() = _scope
    private val _scope = MutableScope()

    private val appInitializers by
    inject<Map<KClass<AppInitializer>, Provider<AppInitializer>>>(AppInitializers)
    private val appServices by
    inject<Map<KClass<AppService>, Provider<AppService>>>(AppServices)

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

    protected open fun createComponent(): Component {
        return applicationComponent {
            modules(esAppModule, esModule)
            modules(this@EsApp.modules())
        }
    }

    protected open fun invokeInitializers() {
        appInitializers
            .filterKeys { shouldInitialize(it) }
            .map {
                println("initialize $it")
                it.value()
            }
            .forEach { it.initialize() }
    }

    protected open fun shouldInitialize(type: KClass<out AppInitializer>): Boolean = true

    protected open fun startAppServices() {
        appServices
            .filterKeys { shouldStartAppService(it) }
            .map { it.value() }
            .forEach { it.start() }
    }

    protected open fun shouldStartAppService(type: KClass<out AppService>): Boolean = true

    protected open fun modules(): List<Module> = emptyList()

}