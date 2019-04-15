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

package com.ivianuu.essentials.apps.glide

import com.ivianuu.essentials.app.appInitializer
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import com.ivianuu.injekt.getProvider
import com.ivianuu.injekt.module

/**
 * Binds dependencies related to this module
 */
val esAppsGlideModule = module {
    appInitializer { AppGlideAppInitializer(get(), get()) }

    factory { AppIconFetcher(it[0], get()) }
    factory { AppIconModelLoader(getProvider()) }
    factory { AppIconModelLoaderFactory(getProvider()) }
}