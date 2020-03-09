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

import com.ivianuu.essentials.util.BoxLoggerAppInitializer
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.map

/**
 * Will be started on app start up and lives as long as the app lives
 */
interface AppService

@QualifierMarker
annotation class AppServices {
    companion object : Qualifier.Element
}

inline fun <reified T : AppService> ComponentBuilder.bindAppServiceIntoMap(
    serviceQualifier: Qualifier = Qualifier.None
) {
    map<String, AppService>(AppServices) {
        put<T>(T::class.java.name, entryValueQualifier = serviceQualifier)
    }
}

fun ComponentBuilder.esAppServicesBindings() {
    map<String, AppInitializer>(mapQualifier = AppInitializers)
}
