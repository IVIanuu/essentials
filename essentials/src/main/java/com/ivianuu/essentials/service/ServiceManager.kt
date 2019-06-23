/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.service

import com.ivianuu.essentials.app.RxJavaAppInitializer
import com.ivianuu.essentials.app.TimberAppInitializer
import com.ivianuu.essentials.ui.twilight.TwilightController
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.injekt.set

class ServiceManager(
    private val serviceProviders: Set<Provider<Service>>
) {

    private val startedServices = mutableListOf<Service>()

    fun startServices() {
        serviceProviders
            .map { it() }
            .forEach {
                it.start()
                startedServices.add(it)
            }
    }

    fun stopServices() {
        startedServices.forEach { it.stop() }
        startedServices.clear()
    }

}

val serviceModule = module {
    set<Service>(AppServices)
    set<Service>(ActivityServices)
    set<Service>(ControllerServices)

    factory { (serviceName: Any) -> ServiceManager(get(serviceName)) }

    bindAppService<TwilightController>()
    bindAppService<TimberAppInitializer>()
    bindAppService<RxJavaAppInitializer>()
    bindAppService<UiVisibleServicesManager>()
}