/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.serialization

import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scoped
import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@Provide object SerializationModule {
  @Provide inline fun <reified T> kSerializer(): KSerializer<T> = serializer()

  @Provide fun serializersModule(
    serializers: Map<KClass<*>, KSerializer<*>>
  ): @Scoped<AppScope> SerializersModule = SerializersModule {
    serializers.forEach {
      contextual<Any>(kClass = it.key.cast(), serializer = it.value.cast())
    }
  }
}

@Tag annotation class InjektSerializer {
  companion object {
    @Provide val defaultSerializers get() = emptyList<Pair<KClass<*>, KSerializer<*>>>()

    @Provide fun <@Spread T : @InjektSerializer KSerializer<S>, S : Any> serializerBinding(
      clazz: KClass<S>,
      serializer: T
    ): Pair<KClass<*>, KSerializer<*>> = clazz to serializer

    @Provide fun <@Spread T : @InjektSerializer KSerializer<S>, S : Any> serializer(
      serializer: T
    ): KSerializer<S> = serializer
  }
}
