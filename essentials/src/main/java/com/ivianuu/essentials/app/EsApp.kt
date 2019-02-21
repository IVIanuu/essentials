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
import com.ivianuu.essentials.injection.injectProviderClassMap
import com.ivianuu.essentials.util.ext.containsFlag
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.androidLogger
import com.ivianuu.injekt.android.applicationComponent
import com.ivianuu.injekt.configureInjekt
import com.ivianuu.injekt.modules
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner
import kotlin.reflect.KClass

/**
 * App
 */
abstract class EsApp : Application(), InjektTrait, ScopeOwner {

    override val component: Component
        get() {
            createComponentIfNeeded()
            return _component
        }

    private lateinit var _component: Component
    private var componentCreated = false

    override val scope: Scope get() = _scope
    private val _scope = MutableScope()

    private val appInitializers
            by injectProviderClassMap<AppInitializer>(APP_INITIALIZERS)
    private val appServices
            by injectProviderClassMap<AppService>(APP_SERVICES)

    override fun onCreate() {
        super.onCreate()
        createComponentIfNeeded()
        onInitialize()
        onStartAppServices()
    }

    protected open fun onCreateComponent() {
        configureInjekt {
            if (applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
                androidLogger()
            }
        }

        _component = applicationComponent {
            modules(esAppModule, esModule)
            modules(this@EsApp.modules())
        }
    }

    protected open fun onInitialize() {
        println("on init $appInitializers")
        appInitializers
            .filterKeys { shouldInitialize(it) }
            .map { it.value.get() }
            .forEach { it.initialize() }
    }

    protected open fun shouldInitialize(type: KClass<out AppInitializer>): Boolean = true

    protected open fun onStartAppServices() {
        appServices
            .filterKeys { shouldStartAppService(it) }
            .map { it.value.get() }
            .forEach { it.start() }
    }

    protected open fun shouldStartAppService(type: KClass<out AppService>): Boolean = true

    protected open fun modules(): List<Module> = emptyList()

    private fun createComponentIfNeeded() {
        if (!componentCreated) {
            componentCreated = true
            onCreateComponent()
        }
    }
}