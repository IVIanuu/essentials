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
import androidx.lifecycle.ProcessLifecycleOwner
import com.ivianuu.essentials.injection.esModule
import com.ivianuu.essentials.service.AppServices
import com.ivianuu.essentials.service.LifecycleServicesManager
import com.ivianuu.essentials.service.ServiceHost
import com.ivianuu.essentials.service.serviceHostModule
import com.ivianuu.essentials.service.services
import com.ivianuu.essentials.util.ext.containsFlag
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektPlugins
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.AndroidLogger
import com.ivianuu.injekt.android.applicationComponent
import com.ivianuu.injekt.inject
import com.ivianuu.injekt.logger
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.ScopeOwner

/**
 * App
 */
abstract class EsApp : Application(), InjektTrait, ScopeOwner, ServiceHost {

    override val component by unsafeLazy {
        configureInjekt()
        createComponent()
    }

    override val scope: Scope get() = _scope
    private val _scope = MutableScope()

    private val services by services(AppServices)

    private val lifecycleServices by inject<LifecycleServicesManager>()

    override fun onCreate() {
        super.onCreate()
        services.startServices()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleServices)
    }

    protected open fun configureInjekt() {
        if (applicationInfo.flags.containsFlag(ApplicationInfo.FLAG_DEBUGGABLE)) {
            InjektPlugins.logger = AndroidLogger()
        }
    }

    protected open fun createComponent(): Component {
        return applicationComponent {
            modules(esAppModule, esModule, serviceHostModule(this@EsApp))
            modules(this@EsApp.modules())
        }
    }

    protected open fun modules(): List<Module> = emptyList()

}