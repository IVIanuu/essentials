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
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Name
import kotlin.reflect.KClass

@Name
annotation class Decoders {
    companion object
}

fun <T : Decoder> ModuleBuilder.bindDecoder(
    name: Any? = null
) {
    //withBinding<T>(name) { bindDecoder() }
}

fun <T : Decoder> BindingContext<T>.bindDecoder() {
    //intoSet<Decoder>(setName = Decoders)
}

@Name
annotation class Fetchers {
    companion object
}

data class FetcherBinding<T : Any>(
    val fetcher: Fetcher<T>,
    val type: KClass<T>
)

fun <F : Fetcher<T>, T : Any> ModuleBuilder.bindFetcher(
    type: KClass<T>,
    name: Any? = null
) {
    //withBinding<F>(name) { bindFetcher(type) }
}

fun <F : Fetcher<T>, T : Any> BindingContext<F>.bindFetcher(type: KClass<T>) {
    /*moduleBuilder.factory {
        FetcherBinding(
            fetcher = get<F>(),
            type = type
        )
    }.also { bbc ->
        moduleBuilder.set<FetcherBinding<*>>(
            Fetchers
        ) {
            add(bbc.key, false)
        }
    }*/
}

@Name
annotation class Mappers {
    companion object
}

data class MapperBinding<T : Any>(
    val mapper: Mapper<T, *>,
    val type: KClass<T>
)

fun <M : Mapper<T, *>, T : Any> ModuleBuilder.bindMapper(
    type: KClass<T>,
    name: Any? = null
) {
    //withBinding<M>(name) { bindMapper(type) }
}

fun <M : Mapper<T, *>, T : Any> BindingContext<M>.bindMapper(type: KClass<T>) {
    /*moduleBuilder.factory {
        MapperBinding(mapper = get<M>(), type = type)
    }.also { bbc ->
        moduleBuilder.set<MapperBinding<*>>(
            Mappers
        ) {
            add(bbc.key, false)
        }

        return this
    }*/
}

@Name
annotation class MeasuredMappers {
    companion object
}

data class MeasuredMapperBinding<T : Any>(
    val mapper: MeasuredMapper<T, *>,
    val type: KClass<T>
)

fun <M : MeasuredMapper<T, *>, T : Any> ModuleBuilder.bindMeasuredMapper(
    type: KClass<T>,
    name: Any? = null
) {
    //withBinding<M>(name = name) { bindMeasuredMapper(type) }
}

fun <M : MeasuredMapper<T, *>, T : Any> BindingContext<M>.bindMeasuredMapper(
    type: KClass<T>
) {
    /*moduleBuilder.factory {
        MeasuredMapperBinding(
            mapper = get<M>(),
            type = type
        )
    }.also { bbc ->
        moduleBuilder.set<MeasuredMapperBinding<*>>(
            MeasuredMappers
        ) {
            add(bbc.key, false)
        }
    }

    return this*/
}
