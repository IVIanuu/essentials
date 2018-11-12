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

package com.ivianuu.essentials.app.glide

import android.app.Application
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.ivianuu.essentials.app.AppService
import javax.inject.Inject

/**
 * Initializes the app icon model loader
 */
class AppGlideInitializer @Inject constructor(
    private val appIconModelLoaderFactory: AppIconModelLoader.Factory,
    private val application: Application
) : AppService {

    override fun start() {
        Glide.get(application).registry
            .append(AppIcon::class.java, Drawable::class.java, appIconModelLoaderFactory)
    }
}