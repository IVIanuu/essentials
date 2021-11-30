/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Provide class SerializerImpl<T>(
  private val json: Json,
  private val serializer: KSerializer<T>,
  private val _defaultData: () -> @InitialOrDefault T
) : Serializer<T> {
  override val defaultData: T
    get() = _defaultData()

  override fun deserialize(serializedData: String): T = catch {
    json.decodeFromString(serializer, serializedData)
  }.getOrElse { throw SerializerException("Couldn't deserialize data", it) }

  override fun serialize(data: T): String = json.encodeToString(serializer, data)
}
