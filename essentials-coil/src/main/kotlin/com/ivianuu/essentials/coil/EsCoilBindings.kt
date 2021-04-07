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
import coil.fetch.Fetcher
import coil.intercept.Interceptor
import coil.map.Mapper
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlin.reflect.KClass

@Given
fun imageLoader(
    @Given appContext: AppContext,
    @Given decoders: Set<Decoder> = emptySet(),
    @Given fetchers: Set<FetcherPair<Any>> = emptySet(),
    @Given interceptors: Set<Interceptor> = emptySet(),
    @Given mappers: Set<MapperPair<Any>> = emptySet(),
): @Scoped<AppGivenScope> ImageLoader = ImageLoader.Builder(appContext)
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

@Given
fun <@Given F : Fetcher<T>, T : Any> fetcherPair(
    @Given instance: F,
    @Given typeClass: KClass<T>
): FetcherPair<Any> = FetcherPair(instance, typeClass) as FetcherPair<Any>

data class FetcherPair<T : Any>(
    val fetcher: Fetcher<T>,
    val type: KClass<T>
)

@Given
fun <@Given M : Mapper<T, V>, T : Any, V : Any> mapperPair(
    @Given instance: M,
    @Given typeClass: KClass<T>
): MapperPair<Any> = MapperPair(instance, typeClass) as MapperPair<Any>

data class MapperPair<T : Any>(
    val mapper: Mapper<T, *>,
    val type: KClass<T>
)
