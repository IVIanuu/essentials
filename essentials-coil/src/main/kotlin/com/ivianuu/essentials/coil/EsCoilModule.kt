/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coil

import coil.ImageLoader
import coil.decode.Decoder
import coil.fetch.Fetcher
import coil.intercept.Interceptor
import coil.key.Keyer
import coil.map.Mapper
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.Scoped
import kotlin.reflect.KClass

@Provide fun imageLoader(
  appContext: AppContext,
  decoderFactories: List<Decoder.Factory>,
  fetcherFactories: List<FetcherPair<*>>,
  keyers: List<KeyerPair<*>>,
  interceptors: List<Interceptor>,
  mappers: List<MapperPair<*>>,
): @Scoped<AppScope> ImageLoader = ImageLoader.Builder(appContext)
  .components {
    decoderFactories.forEach { add(it) }
    interceptors.forEach { add(it) }
    keyers.forEach { add(it.keyer as Keyer<Any>, it.type.java as Class<Any>) }
    fetcherFactories
      .forEach { add(it.factory as Fetcher.Factory<Any>, it.type.java as Class<Any>) }
    mappers
      .forEach { add(it.mapper as Mapper<Any, Any>, it.type.java as Class<Any>) }
  }
  .build()

@Provide val defaultDecoderFactories get() = emptyList<Decoder.Factory>()

@Provide fun <@Spread F : Fetcher.Factory<T>, T : Any> fetcherFactoryPair(
  instance: F,
  typeClass: KClass<T>
): FetcherPair<*> = FetcherPair(instance, typeClass)

@Provide val defaultFetcherFactories get() = emptyList<FetcherPair<*>>()

data class FetcherPair<T : Any>(val factory: Fetcher.Factory<T>, val type: KClass<T>)

@Provide fun <@Spread M : Mapper<T, V>, T : Any, V : Any> mapperPair(
  instance: M,
  typeClass: KClass<T>
): MapperPair<*> = MapperPair(instance, typeClass)

data class MapperPair<T : Any>(val mapper: Mapper<T, *>, val type: KClass<T>)

@Provide val defaultMappers get() = emptyList<MapperPair<*>>()

@Provide val defaultInterceptors get() = emptyList<Interceptor>()

@Provide fun <@Spread K : Keyer<T>, T : Any> keyerPair(
  instance: K,
  typeClass: KClass<T>
): KeyerPair<*> = KeyerPair(instance, typeClass)

data class KeyerPair<T : Any>(val keyer: Keyer<T>, val type: KClass<T>)

@Provide val defaultKeyers get() = emptyList<KeyerPair<*>>()
