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
import android.graphics.drawable.Drawable
import android.os.Looper
import android.view.View
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.ivianuu.contributor.view.HasViewInjector
import com.ivianuu.essentials.util.AppIcon
import com.ivianuu.essentials.util.AppIconModelLoader
import com.ivianuu.essentials.util.analytics.Analytics
import com.ivianuu.essentials.util.analytics.DebugAnalyticsLogger
import com.ivianuu.essentials.util.analytics.FabricAnalyticsLogger
import com.ivianuu.essentials.util.ext.containsFlag
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

/**
 * App
 */
abstract class BaseApp : DaggerApplication(), HasViewInjector {

    @Inject internal lateinit var appServices: Set<@JvmSuppressWildcards AppService>
    @Inject internal lateinit var appIconModelLoaderFactory: AppIconModelLoader.Factory
    @Inject internal lateinit var debugAnalyticsLogger: DebugAnalyticsLogger
    @Inject internal lateinit var fabricAnalyticsLogger: FabricAnalyticsLogger

    @Inject internal lateinit var viewInjector: DispatchingAndroidInjector<View>

    @Inject internal lateinit var workerFactory: Provider<WorkerFactory>

    protected open val initTimber = true
    protected open val initFabric = true
    protected open val initGlide = true
    protected open val initRxJava = true
    protected open val initWorkManager = true

    override fun onCreate() {
        super.onCreate()

        if (initWorkManager) {
            WorkManager.initialize(
                this,
                Configuration.Builder().setWorkerFactory(workerFactory.get()).build()
            )
        }

        val isDebuggable = applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)

        if (isDebuggable && initTimber) {
            Timber.plant(Timber.DebugTree())
            Analytics.addLogger(debugAnalyticsLogger)
        } else if (!isDebuggable && initFabric) {
            Fabric.with(this, Crashlytics())
            Analytics.addLogger(fabricAnalyticsLogger)
        }

        if (initGlide) {
            Glide.get(this).registry
                .append(AppIcon::class.java, Drawable::class.java, appIconModelLoaderFactory)
        }

        if (initRxJava) {
            val scheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
        }

        appServices.forEach { startAppService(it) }
    }

    override fun viewInjector(): AndroidInjector<View> = viewInjector

    protected open fun startAppService(appService: AppService) {
        appService.start()
    }
}