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
import com.ivianuu.essentials.Scoped
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlin.reflect.KClass

object EsCoilModule {
  @Provide fun imageLoader(
    appContext: AppContext,
    decoderFactories: List<Decoder.Factory>,
    fetcherFactories: List<FetcherFactoryBinding<*>>,
    keyers: List<KeyerBinding<*>>,
    interceptors: List<Interceptor>,
    mappers: List<MapperBinding<*>>,
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

  @Provide val defaultFetcherFactoryBindings get() = emptyList<FetcherFactoryBinding<*>>()

  @Provide fun <@Spread F : Fetcher.Factory<T>, T : Any> fetcherBinding(
    type: KClass<T>,
    instance: F
  ): FetcherFactoryBinding<*> = FetcherFactoryBinding(type, instance)

  data class FetcherFactoryBinding<T : Any>(
    val type: KClass<T>,
    val factory: Fetcher.Factory<T>
  )

  data class MapperBinding<T : Any>(val type: KClass<T>, val mapper: Mapper<T, *>)

  @Provide fun <@Spread M : Mapper<T, V>, T : Any, V : Any> mapperBinding(
    type: KClass<T>,
    instance: M
  ): MapperBinding<*> = MapperBinding(type, instance)

  @Provide val defaultMappers get() = emptyList<MapperBinding<*>>()

  @Provide val defaultInterceptors get() = emptyList<Interceptor>()

  data class KeyerBinding<T : Any>(val type: KClass<T>, val keyer: Keyer<T>)

  @Provide fun <@Spread K : Keyer<T>, T : Any> keyerBinding(
    type: KClass<T>,
    instance: K
  ): KeyerBinding<*> = KeyerBinding(type, instance)

  @Provide val defaultKeyers get() = emptyList<KeyerBinding<*>>()
}
