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

package com.ivianuu.essentials.coil

import coil.decode.Decoder
import coil.fetch.Fetcher
import coil.map.Mapper
import coil.map.MeasuredMapper
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.set
import com.ivianuu.injekt.transient
import kotlin.reflect.KClass

@Module
inline fun <D : Decoder> coilDecoder() {
    transient<D>()
    set<Decoder> { add<D>() }
}

@Module
inline fun <F : Fetcher<T>, reified T : Any> coilFetcher() {
    transient<F>()
    transient { fetcher: F ->
        FetcherBinding(
            fetcher = fetcher,
            type = T::class
        )
    }
    set<FetcherBinding<*>> {
        add<FetcherBinding<T>>()
    }
}

data class FetcherBinding<T : Any>(
    val fetcher: Fetcher<T>,
    val type: KClass<T>
)

@Module
inline fun <M : Mapper<T, *>, reified T : Any> coilMapper() {
    transient<M>()
    transient { mapper: M ->
        MapperBinding(
            mapper = mapper,
            type = T::class
        )
    }
    set<MapperBinding<*>> {
        add<MapperBinding<T>>()
    }
}

data class MapperBinding<T : Any>(
    val mapper: Mapper<T, *>,
    val type: KClass<T>
)

@Module
inline fun <M : MeasuredMapper<T, *>, reified T : Any> coilMeasuredMapper() {
    transient<M>()
    transient { mapper: M ->
        MeasuredMapperBinding(
            mapper = mapper,
            type = T::class
        )
    }
    set<MeasuredMapperBinding<*>> {
        add<MeasuredMapperBinding<T>>()
    }
}

data class MeasuredMapperBinding<T : Any>(
    val mapper: MeasuredMapper<T, *>,
    val type: KClass<T>
)
