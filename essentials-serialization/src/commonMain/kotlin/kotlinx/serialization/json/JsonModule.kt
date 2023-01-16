/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.json

import com.ivianuu.essentials.AppScope
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import kotlinx.serialization.modules.SerializersModule

object JsonModule {
  @Provide fun json(serializersModule: SerializersModule): @Scoped<AppScope> Json = Json {
    ignoreUnknownKeys = true
    this.serializersModule = serializersModule
  }
}
