package com.ivianuu.essentials.time

import kotlin.time.*

actual fun Double.toDuration(): Duration =
  Duration::class.java.getDeclaredConstructor(Double::class.java)
    .also { it.isAccessible = true }
    .newInstance(this)

actual fun Duration.toDouble(): Double = javaClass.declaredFields
  .first { it.type == Double::class.java }
  .also { it.isAccessible = true }
  .get(this)!! as Double
