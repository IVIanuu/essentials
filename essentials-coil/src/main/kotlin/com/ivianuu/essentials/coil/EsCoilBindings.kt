/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coil

import coil.CoilAccessor
import coil.ImageLoader
import coil.decode.Decoder
import coil.fetch.Fetcher
import coil.intercept.Interceptor
import coil.map.Mapper
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.common.Scoped
import kotlin.reflect.KClass

@Provide fun imageLoader(
  context: AppContext,
  decoders: List<Decoder>,
  fetchers: List<FetcherPair<*>>,
  interceptors: List<Interceptor>,
  mappers: List<MapperPair<*>>,
): @Scoped<AppScope> ImageLoader = ImageLoader.Builder(context)
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

@Provide fun <@Spread F : Fetcher<T>, T : Any> fetcherPair(
  instance: F,
  typeClass: KClass<T>
): FetcherPair<*> = FetcherPair(instance, typeClass)

data class FetcherPair<T : Any>(
  val fetcher: Fetcher<T>,
  val type: KClass<T>
)

@Provide fun defaultFetchers() = emptyList<FetcherPair<*>>()

@Provide fun <@Spread M : Mapper<T, V>, T : Any, V : Any> mapperPair(
  instance: M,
  typeClass: KClass<T>
): MapperPair<*> = MapperPair(instance, typeClass)

data class MapperPair<T : Any>(
  val mapper: Mapper<T, *>,
  val type: KClass<T>
)

@Provide fun defaultMappers() = emptyList<MapperPair<*>>()

@Provide fun defaultDecoders() = emptyList<Decoder>()

@Provide fun defaultInterceptors() = emptyList<Interceptor>()
