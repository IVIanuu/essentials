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
import android.os.Looper
import com.ivianuu.essentials.injection.GlobalComponentHolder
import com.ivianuu.essentials.injection.esModules
import com.ivianuu.essentials.util.ext.containsFlag
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.androidLogger
import com.ivianuu.statestore.StateStorePlugins
import com.ivianuu.statestore.android.MAIN_THREAD_EXECUTOR
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

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

    override fun onCreate() {
        super.onCreate()
        onCreateComponent()
        onInitialize()
    }

    protected open fun onCreateComponent() {
        InjektPlugins.logger = androidLogger()
        _component = component(
            modules = implicitModules() + modules(),
            dependencies = dependencies()
        ).also { GlobalComponentHolder.initialize(it) }
    }

    protected open fun onInitialize() {
        initializeRxJava()
        initializeStateStore()
        initializeTimber()
    }

    protected open fun initializeRxJava() {
        val scheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    protected open fun initializeStateStore() {
        StateStorePlugins.defaultCallbackExecutor = MAIN_THREAD_EXECUTOR
    }

    protected open fun initializeTimber() {
        val isDebuggable = applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)
        if (isDebuggable) {
            Timber.plant(Timber.DebugTree())
        }
    }

    protected open fun dependencies(): List<Component> = emptyList()

    protected open fun implicitModules() =
        listOf(esAppModule(this)) + esModules()

    protected open fun modules(): List<Module> = emptyList()

    private fun createComponentIfNeeded() {
        if (!componentCreated) {
            componentCreated = true
            onCreateComponent()
        }
    }
}