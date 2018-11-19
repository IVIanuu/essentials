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
import com.ivianuu.injectors.CompositeInjectors
import com.ivianuu.injectors.HasInjectors
import com.ivianuu.injectors.Injector
import com.ivianuu.injectors.Injectors
import javax.inject.Inject
import javax.inject.Provider

/**
 * App
 */
abstract class BaseApp : Application(), HasInjectors {

    @Inject internal lateinit var _injectors: CompositeInjectors
    @Inject
    internal lateinit var appInitializer: Map<Class<out AppInitializer>, @JvmSuppressWildcards Provider<AppInitializer>>
    @Inject
    internal lateinit var appServices: Map<Class<out AppService>, @JvmSuppressWildcards Provider<AppService>>

    override val injectors: Injectors
        get() {
            injectIfNeeded()
            return _injectors
        }

    private var injected = false

    override fun onCreate() {
        injectIfNeeded()
        super.onCreate()

        appInitializer
            .filter { shouldInitializeAppInitializer(it.key) }
            .map { it.value.get() }
            .forEach { it.initialize(this) }

        appServices
            .filter { shouldStartAppService(it.key) }
            .map { it.value.get() }
            .forEach { it.start() }
    }

    protected open fun shouldInitializeAppInitializer(clazz: Class<out AppInitializer>) = true

    protected open fun shouldStartAppService(clazz: Class<out AppService>) = true

    protected abstract fun applicationInjector(): Injector<out BaseApp>

    private fun injectIfNeeded() {
        if (!injected) {
            injected = true
            (applicationInjector() as Injector<BaseApp>)
                .inject(this)
        }
    }
}