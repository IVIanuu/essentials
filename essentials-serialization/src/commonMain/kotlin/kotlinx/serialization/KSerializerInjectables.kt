/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization

import com.ivianuu.injekt.*
import kotlin.reflect.*

object SerializationInjectables {
  @Provide inline fun <T> kSerializer(type: KTypeT<T>): KSerializer<T> =
    serializer(type) as KSerializer<T>
}
