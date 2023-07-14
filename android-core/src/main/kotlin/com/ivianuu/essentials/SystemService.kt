package com.ivianuu.essentials

import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlin.reflect.KClass

@Tag annotation class SystemService {
  @Provide companion object {
    @Provide fun <T : Any> systemService(clazz: KClass<T>, context: AppContext): @SystemService T =
      context.getSystemService(clazz.java)!!
  }
}
