package com.ivianuu.essentials.time

import kotlin.time.*

actual fun Long.toDuration(): Duration =
  Duration::class.java.getDeclaredConstructor(Long::class.java)
    .also { it.isAccessible = true }
    .newInstance(this)

actual fun Duration.toLong(): Long = javaClass.declaredFields
  .first { it.type == Long::class.java }
  .also { it.isAccessible = true }
  .get(this)!! as Long
