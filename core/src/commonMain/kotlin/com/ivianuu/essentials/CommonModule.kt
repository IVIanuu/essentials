package com.ivianuu.essentials

import com.ivianuu.injekt.*
import kotlin.reflect.*

@Provide object CommonModule {
  @Provide fun <K, V> pairsToMap(pairs: List<Pair<K, V>>) = pairs.toMap()

  @Provide inline fun <reified T : Any> kclass(): KClass<T> = T::class
}
