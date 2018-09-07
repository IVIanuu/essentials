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
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.ivianuu.essentials.util.AppIcon
import com.ivianuu.essentials.util.AppIconModelLoader
import com.ivianuu.essentials.util.analytics.Analytics
import com.ivianuu.essentials.util.analytics.DebugAnalyticsLogger
import com.ivianuu.essentials.util.analytics.FabricAnalyticsLogger
import com.ivianuu.essentials.util.ext.containsFlag
import com.ivianuu.essentials.util.rx.EnsureMainThreadScheduler
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import io.reactivex.android.plugins.RxAndroidPlugins
import timber.log.Timber
import javax.inject.Inject

/**
 * App
 */
abstract class BaseApp : DaggerApplication() {

    @Inject internal lateinit var appServices: Set<@JvmSuppressWildcards AppService>
    @Inject internal lateinit var appIconModelLoaderFactory: AppIconModelLoader.Factory
    @Inject internal lateinit var debugAnalyticsLogger: DebugAnalyticsLogger
    @Inject internal lateinit var fabricAnalyticsLogger: FabricAnalyticsLogger

    protected open val initTimber = true
    protected open val initFabric = true
    protected open val initGlide = true
    protected open val initRxJava = true

    override fun onCreate() {
        super.onCreate()

        if (applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
            if (initTimber) {
                Timber.plant(Timber.DebugTree())
                Analytics.addLogger(debugAnalyticsLogger)
            }
        } else {
            if (initFabric) {
                Fabric.with(this, Crashlytics())
                Analytics.addLogger(fabricAnalyticsLogger)
            }
        }

        if (initGlide) {
            Glide.get(this).registry
                .append(AppIcon::class.java, Bitmap::class.java, appIconModelLoaderFactory)
        }

        if (initRxJava) {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { EnsureMainThreadScheduler.INSTANCE }
        }

        appServices.forEach { startAppService(it) }
    }

    protected open fun startAppService(appService: AppService) {
        appService.start()
    }
}