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

import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.set

/**
 * Will be instantiated on app start up
 * Can be used to initialize global stuff like logging
 *
 * ´´´
 * @BindAppInitializer
 * fun initializeAnalytics() {
 *     Analytics.initialize(Logger())
 * }
 * ´´´
 */
@BindingEffect(ApplicationComponent::class)
annotation class AppInitializer {
    companion object {
        @Module
        operator fun <T : () -> Unit> invoke() {
            set<@AppInitializers Set<() -> Unit>, () -> Unit> {
                add<T>()
            }
        }
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class AppInitializers

@Module
fun EsAppInitializerModule() {
    installIn<ApplicationComponent>()
    set<@AppInitializers Set<() -> Unit>, () -> Unit>()
}

@Reader
fun runInitializers() {
    d("Initialize")
    get<@AppInitializers Set<() -> Unit>>()
        .forEach { it() }
}
