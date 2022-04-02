/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization

import com.ivianuu.injekt.Provide

object SerializationInjectables {
  @Provide inline fun <reified T> kSerializer(): KSerializer<T> = serializer()
}
