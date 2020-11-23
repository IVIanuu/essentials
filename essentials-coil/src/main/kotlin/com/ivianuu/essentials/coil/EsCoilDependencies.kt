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
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlin.reflect.KClass

@Binding(ApplicationComponent::class)
fun imageLoader(
    applicationContext: ApplicationContext,
    decoders: Decoders,
    fetchers: Fetchers,
    interceptors: Interceptors,
    mappers: Mappers,
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

@Effect
annotation class DecoderBinding {
    companion object {
        @SetElements
        fun <T : Decoder> intoSet(instance: @ForEffect T): Decoders = setOf(instance)
    }
}

typealias Decoders = Set<Decoder>

@SetElements
fun defaultDecoders(): Decoders = emptySet()

@Effect
annotation class FetcherBinding {
    companion object {
        @SetElements
        inline fun <reified F : Fetcher<T>, reified T : Any> intoSet(instance: @ForEffect F): Fetchers =
            setOf(FetcherPair(instance, T::class))
    }
}

data class FetcherPair<T : Any>(
    val fetcher: Fetcher<T>,
    val type: KClass<T>
)

typealias Fetchers = Set<FetcherPair<*>>

@SetElements
fun defaultFetchers(): Fetchers = emptySet()

@Effect
annotation class InterceptorBinding {
    companion object {
        @SetElements
        fun <T : Interceptor> intoSet(instance: @ForEffect T): Interceptors = setOf(instance)
    }
}

typealias Interceptors = Set<Interceptor>

@SetElements
fun defaultInterceptors(): Interceptors = emptySet()

@Effect
annotation class MapperBinding {
    companion object {
        @SetElements
        inline fun <reified M : Mapper<T, V>, reified T : Any, reified V : Any> intoSet(instance: @ForEffect M): Mappers =
            setOf(MapperPair(instance, T::class))
    }
}

typealias Mappers = Set<MapperPair<*>>

@SetElements
fun defaultMappers(): Mappers = emptySet()

data class MapperPair<T : Any>(
    val mapper: Mapper<T, *>,
    val type: KClass<T>
)
