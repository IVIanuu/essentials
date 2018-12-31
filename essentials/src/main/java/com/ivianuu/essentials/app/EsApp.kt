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
import com.ivianuu.essentials.injection.GlobalComponentHolder
import com.ivianuu.essentials.injection.bindInstanceModule
import com.ivianuu.essentials.injection.esModule

import com.ivianuu.essentials.injection.multibinding.ClassMultiBindingMap
import com.ivianuu.essentials.injection.multibinding.toProviderMap
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.androidLogger
import kotlin.reflect.KClass

/**
 * App
 */
abstract class EsApp : Application(), ComponentHolder {

    override val component: Component
        get() {
            createComponentIfNeeded()
            return _component
        }

    private lateinit var _component: Component
    private var componentCreated = false

    private val appInitializers by unsafeLazy {
        get<ClassMultiBindingMap<AppInitializer>>(APP_INITIALIZERS)
            .toProviderMap()
    }
    private val appServices by unsafeLazy {
        get<ClassMultiBindingMap<AppService>>(APP_SERVICES)
            .toProviderMap()
    }

    override fun onCreate() {
        super.onCreate()
        createComponentIfNeeded()
        onInitialize()
        onStartAppServices()
    }

    protected open fun onCreateComponent() {
        configureInjekt { androidLogger() }

        _component = component {
            dependencies(this@EsApp.dependencies())
            modules(
                bindInstanceModule(this@EsApp),
                esAppModule(this@EsApp), esModule
            )
            modules(this@EsApp.modules())
            GlobalComponentHolder.initialize(this)
        }
    }

    protected open fun onInitialize() {
        appInitializers
            .filterKeys { shouldInitialize(it) }
            .map { it.value.get() }
            .forEach { it.initialize(this) }
    }

    protected open fun shouldInitialize(type: KClass<out AppInitializer>) = true

    protected open fun onStartAppServices() {
        appServices
            .filterKeys { shouldStartAppService(it) }
            .map { it.value.get() }
            .forEach { it.start() }
    }

    protected open fun shouldStartAppService(type: KClass<out AppService>) = true

    protected open fun dependencies(): List<Component> = emptyList()

    protected open fun modules(): List<Module> = emptyList()

    private fun createComponentIfNeeded() {
        if (!componentCreated) {
            componentCreated = true
            onCreateComponent()
        }
    }
}