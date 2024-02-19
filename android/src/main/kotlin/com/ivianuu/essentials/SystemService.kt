package com.ivianuu.essentials

import android.content.Context
import com.ivianuu.injekt.*
import kotlin.reflect.*

@Tag annotation class SystemService {
  @Provide companion object {
    @Provide fun <T : Any> systemService(clazz: KClass<T>, context: Context): @SystemService T =
      context.getSystemService(clazz.java)!!
  }
}
