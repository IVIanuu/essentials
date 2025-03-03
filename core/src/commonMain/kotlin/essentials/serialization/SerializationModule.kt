/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.serialization

import essentials.*
import injekt.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import kotlin.reflect.*

@Provide object SerializationModule {
  @Provide fun json(serializersModule: SerializersModule): @Scoped<AppScope> Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    this.serializersModule = serializersModule
  }

  @Provide inline fun <reified T> kSerializer(): KSerializer<T> = serializer()

  @Provide fun serializersModule(
    serializers: Map<KClass<*>, KSerializer<*>>
  ): @Scoped<AppScope> SerializersModule = SerializersModule {
    serializers.forEach {
      contextual<Any>(kClass = it.key.cast(), serializer = it.value.cast())
    }
  }
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class InjektSerializer {
  @Provide companion object {
    @Provide val defaultSerializers get() = emptyList<Pair<KClass<*>, KSerializer<*>>>()

    @Provide fun <@AddOn T : @InjektSerializer KSerializer<S>, S : Any> serializerBinding(
      clazz: KClass<S>,
      serializer: T
    ): Pair<KClass<*>, KSerializer<*>> = clazz to serializer

    @Provide fun <@AddOn T : @InjektSerializer KSerializer<S>, S : Any> serializer(
      serializer: T
    ): KSerializer<S> = serializer
  }
}
