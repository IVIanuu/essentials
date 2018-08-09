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
import androidx.work.Worker
import com.ivianuu.essentials.injection.KtHasViewInjector
import com.ivianuu.essentials.injection.KtHasWorkerInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

/**
 * App
 */
abstract class BaseApp : DaggerApplication(), KtHasViewInjector, KtHasWorkerInjector {

    @Inject internal lateinit var appInitializers: Set<@JvmSuppressWildcards AppInitializer>
    @Inject internal lateinit var appServices: Set<@JvmSuppressWildcards AppService>

    @Inject override lateinit var viewInjector: DispatchingAndroidInjector<View>
    @Inject override lateinit var workerInjector: DispatchingAndroidInjector<Worker>

    override fun onCreate() {
        super.onCreate()
        appInitializers.forEach { initializeAppInitializer(it) }
        appServices.forEach { startAppService(it) }
    }

    protected open fun initializeAppInitializer(appInitializer: AppInitializer) {
        appInitializer.init(this)
    }

    protected open fun startAppService(appService: AppService) {
        appService.start()
    }
}