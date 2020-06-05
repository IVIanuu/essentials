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

package com.ivianuu.essentials.app

import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.BindingAdapter
import com.ivianuu.injekt.composition.BindingAdapterFunction
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.map
import com.ivianuu.injekt.scoped
import kotlin.reflect.KClass

/**
 * Will be started on app start up and lives as long as the app lives
 */
interface AppService

@BindingAdapter(ApplicationComponent::class)
annotation class BindAppService

@BindingAdapterFunction(BindAppService::class)
@Module
inline fun <reified T : AppService> appService() {
    scoped<T>()
    map<KClass<out AppService>, AppService> {
        put<T>(T::class)
    }
}

@Module
fun esAppServiceInjectionModule() {
    installIn<ApplicationComponent>()
    map<KClass<out AppService>, AppService>()
}

@ApplicationScoped
class AppServiceRunner(
    private val logger: Logger,
    private val services: Map<KClass<out AppService>, @Lazy () -> AppService>
) {
    init {
        services
            .forEach {
                logger.d(tag = "Services", message = "start service ${it.key.java.name}")
                it.value()
            }
    }
}
