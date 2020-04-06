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
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.set
import com.ivianuu.injekt.factory
import com.ivianuu.injekt.get
import kotlin.reflect.KClass

inline fun <reified D : Decoder> ComponentBuilder.bindDecoderIntoSet(
    decoderQualifier: Qualifier = Qualifier.None
) {
    val finalQualifier = Qualifier(D::class) + decoderQualifier
    set<Decoder>(setQualifier = Fetchers) {
        add<Decoder>(elementQualifier = finalQualifier)
    }
}

@QualifierMarker
annotation class Decoders {
    companion object : Qualifier.Element
}

inline fun <reified F : Fetcher<T>, reified T : Any> ComponentBuilder.bindFetcherIntoMap(
    mapperQualifier: Qualifier = Qualifier.None
) {
    val finalQualifier = Qualifier(T::class) + mapperQualifier
    factory(qualifier = finalQualifier) {
        FetcherBinding(
            fetcher = get<F>(qualifier = mapperQualifier),
            type = T::class
        )
    }
    set<FetcherBinding<*>>(setQualifier = Fetchers) {
        add<FetcherBinding<T>>(elementQualifier = finalQualifier)
    }
}

@QualifierMarker
annotation class Fetchers {
    companion object : Qualifier.Element
}

data class FetcherBinding<T : Any>(
    val fetcher: Fetcher<T>,
    val type: KClass<T>
)

inline fun <reified M : Mapper<T, *>, reified T : Any> ComponentBuilder.bindMapperIntoMap(
    mapperQualifier: Qualifier = Qualifier.None
) {
    val finalQualifier = Qualifier(T::class) + mapperQualifier
    factory(qualifier = finalQualifier) {
        MapperBinding(
            mapper = get<M>(qualifier = mapperQualifier),
            type = T::class
        )
    }
    set<MapperBinding<*>>(setQualifier = Mappers) {
        add<MapperBinding<T>>(elementQualifier = finalQualifier)
    }
}

data class MapperBinding<T : Any>(
    val mapper: Mapper<T, *>,
    val type: KClass<T>
)

@QualifierMarker
annotation class Mappers {
    companion object : Qualifier.Element
}

inline fun <reified M : MeasuredMapper<T, *>, reified T : Any> ComponentBuilder.bindMeasuredMapperIntoMap(
    mapperQualifier: Qualifier = Qualifier.None
) {
    val finalQualifier = Qualifier(T::class) + mapperQualifier
    factory(qualifier = finalQualifier) {
        MeasuredMapperBinding(
            mapper = get<M>(qualifier = mapperQualifier),
            type = T::class
        )
    }
    set<MeasuredMapperBinding<*>>(setQualifier = MeasuredMappers) {
        add<MeasuredMapperBinding<T>>(elementQualifier = finalQualifier)
    }
}

data class MeasuredMapperBinding<T : Any>(
    val mapper: MeasuredMapper<T, *>,
    val type: KClass<T>
)

@QualifierMarker
annotation class MeasuredMappers {
    companion object : Qualifier.Element
}
