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
import com.bumptech.glide.Glide
import com.ivianuu.essentials.util.AppIcon
import com.ivianuu.essentials.util.AppIconModelLoader
import com.ivianuu.essentials.util.ext.containsFlag
import com.ivianuu.statestore.StateStorePlugins
import com.ivianuu.statestore.android.MAIN_THREAD_EXECUTOR
import dagger.android.support.DaggerApplication
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * App
 */
abstract class BaseApp : DaggerApplication() {

    @Inject internal lateinit var appServices: Set<@JvmSuppressWildcards AppService>
    @Inject internal lateinit var appIconModelLoaderFactory: AppIconModelLoader.Factory

    protected open val initTimber get() = true
    protected open val initGlide get() = true
    protected open val initRxJava get() = true
    protected open val initStateStore get() = true

    override fun onCreate() {
        super.onCreate()

        val isDebuggable = applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)

        if (isDebuggable && initTimber) {
            Timber.plant(Timber.DebugTree())
        }

        if (initRxJava) {
            val scheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
        }

        if (initStateStore) {
            StateStorePlugins.defaultCallbackExecutor = MAIN_THREAD_EXECUTOR
        }

        if (initGlide) {
            Glide.get(this).registry
                .append(AppIcon::class.java, Drawable::class.java, appIconModelLoaderFactory)
        }

        appServices.forEach { startAppService(it) }
    }

    protected open fun startAppService(appService: AppService) {
        appService.start()
    }
}