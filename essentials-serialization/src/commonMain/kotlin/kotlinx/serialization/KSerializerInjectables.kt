/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import kotlin.reflect.KTypeT

object SerializationInjectables {
  @Provide inline fun <T> kSerializer(type: KTypeT<T>): KSerializer<T> =
    serializer(type).cast()
}
