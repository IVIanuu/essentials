package com.ivianuu.essentials

import com.ivianuu.injekt.*
import kotlin.reflect.*

@Tag annotation class SystemService {
  @Provide companion object {
    @Provide fun <T : Any> systemService(clazz: KClass<T>, appContext: AppContext): @SystemService T =
      appContext.getSystemService(clazz.java)!!
  }
}
