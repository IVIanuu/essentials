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
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.component.AppComponent
import kotlin.reflect.KClass

@Scoped<AppComponent>
@Given
fun imageLoader(
    @Given appContext: AppContext,
    @Given decoders: Set<Decoder>,
    @Given fetchers: Set<FetcherPair<Any>>,
    @Given interceptors: Set<Interceptor>,
    @Given mappers: Set<MapperPair<Any>>,
): ImageLoader = ImageLoader.Builder(appContext)
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

@Qualifier
annotation class DecoderBinding

@Given
fun <@Given T : @DecoderBinding Decoder> decoderBindingImpl(@Given instance: T): Decoder = instance

@Qualifier
annotation class FetcherBinding

@Given
inline fun <@Given reified F : @FetcherBinding Fetcher<T>, reified T : Any> fetcherBindingImpl(
    @Given instance: F
): FetcherPair<Any> = FetcherPair(instance, T::class) as FetcherPair<Any>

data class FetcherPair<T : Any>(
    val fetcher: Fetcher<T>,
    val type: KClass<T>
)

@Qualifier
annotation class InterceptorBinding

@Given
fun <@Given T : @InterceptorBinding Interceptor> interceptorBindingImpl(
    @Given instance: T
): Interceptor = instance

@Qualifier
annotation class MapperBinding

@Given
inline fun <@Given reified M : @MapperBinding Mapper<T, V>, reified T : Any, reified V : Any> mapperBindingImpl(
    @Given instance: M
): MapperPair<Any> = MapperPair(instance, T::class) as MapperPair<Any>

data class MapperPair<T : Any>(
    val mapper: Mapper<T, *>,
    val type: KClass<T>
)
