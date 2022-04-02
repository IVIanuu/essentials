/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.time

import kotlin.time.Duration

actual fun Long.toDuration(): Duration =
  Duration::class.java.getDeclaredConstructor(Long::class.java)
    .also { it.isAccessible = true }
    .newInstance(this)

actual fun Duration.toLong(): Long = javaClass.declaredFields
  .first { it.type == Long::class.java }
  .also { it.isAccessible = true }
  .get(this)!! as Long
