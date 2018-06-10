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

import android.view.View
import com.ivianuu.essentials.injection.KtHasViewInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject
import javax.inject.Provider

/**
 * App
 */
abstract class BaseApp : DaggerApplication(), KtHasViewInjector {

    @Inject internal lateinit var appInitializers: Set<@JvmSuppressWildcards Provider<AppInitializer>>
    @Inject internal lateinit var appServices: Set<@JvmSuppressWildcards Provider<AppService>>

    @Inject override lateinit var viewInjector: DispatchingAndroidInjector<View>

    override fun onCreate() {
        super.onCreate()
        appInitializers.forEach(this::initializeAppInitializer)
        appServices.forEach(this::startAppService)
    }

    protected open fun initializeAppInitializer(appInitializer: Provider<AppInitializer>) {
        appInitializer.get().init(this)
    }

    protected open fun startAppService(appService: Provider<AppService>) {
        appService.get().start()
    }
}