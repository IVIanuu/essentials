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

package com.ivianuu.essentials.android.util

import com.ivianuu.essentials.app.AppInitializer
import com.ivianuu.essentials.store.Logger
import com.ivianuu.essentials.store.logger
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Provider

@Factory
class EsBoxLogger(private val esLogger: com.ivianuu.essentials.util.Logger) : Logger {
    override fun log(message: String) {
        esLogger.d(message = message, tag = "Box")
    }
}

@Factory
class BoxLoggerAppInitializer(
    buildInfo: BuildInfo,
    esBoxLoggerProvider: Provider<EsBoxLogger>
) : AppInitializer {
    init {
        if (buildInfo.isDebug) logger = esBoxLoggerProvider()
    }
}