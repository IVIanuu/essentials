/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.coil

import coil.CoilAccessor
import coil.ImageLoader
import coil.decode.Decoder
import coil.intercept.Interceptor
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent

@Binding(ApplicationComponent::class)
fun imageLoader(
    applicationContext: ApplicationContext,
    decoders: Set<Decoder>,
    fetchers: Set<FetcherBinding<*>>,
    interceptors: Set<Interceptor>,
    mappers: Set<MapperBinding<*>>,
): ImageLoader {
    return ImageLoader.Builder(applicationContext)
        .componentRegistry {
            decoders.forEach { add(it) }
            interceptors.forEach { add(it) }
            fetchers
                .forEach { binding ->
                    CoilAccessor.add(this, binding.type.java, binding.fetcher)
                }
            mappers
                .forEach { binding ->
                    CoilAccessor.add(this, binding.type.java, binding.mapper)
                }
        }
        .build()
}

@SetElements
fun defaultDecoders(): Set<Decoder> = emptySet()

@SetElements
fun defaultFetchers(): Set<FetcherBinding<*>> = emptySet()

@SetElements
fun defaultInterceptors(): Set<Interceptor> = emptySet()

@SetElements
fun defaultMappers(): Set<MapperBinding<*>> = emptySet()
