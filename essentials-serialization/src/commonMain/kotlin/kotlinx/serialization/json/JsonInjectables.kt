/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.json

import com.ivianuu.injekt.Provide

object JsonInjectables {
  @Provide val json = Json { ignoreUnknownKeys = true }
}

