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

import android.content.pm.ApplicationInfo
import android.view.View
import com.crashlytics.android.Crashlytics
import com.ivianuu.daggerextensions.view.HasViewInjector
import com.ivianuu.essentials.internal.EssentialsService
import com.ivianuu.essentials.util.EnsureMainThreadScheduler
import com.ivianuu.essentials.util.analytics.Analytics
import com.ivianuu.essentials.util.ext.containsFlag
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import io.reactivex.android.plugins.RxAndroidPlugins
import timber.log.Timber
import javax.inject.Inject

/**
 * App
 */
abstract class BaseApp : DaggerApplication(), HasViewInjector {

    @Inject internal lateinit var essentialsServices: Set<@JvmSuppressWildcards EssentialsService>

    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    override fun onCreate() {
        super.onCreate()

        if (isDebug()) {
            plantTimber()
        } else {
            initializeFabric()
        }

        RxAndroidPlugins.onMainThreadScheduler(EnsureMainThreadScheduler.INSTANCE)

        Analytics.log("app started")
    }

    override fun viewInjector() = viewInjector

    protected open fun plantTimber() {
        Timber.plant(Timber.DebugTree())
    }

    protected open fun initializeFabric() {
        Fabric.with(this, Crashlytics())
    }

    protected open fun isDebug() =
        applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)
}