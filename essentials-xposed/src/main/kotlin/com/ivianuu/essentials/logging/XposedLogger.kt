/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

@Provide class XposedLogger(private val logTag: XposedLogTag) : Logger {
  override val isEnabled: LoggingEnabled
    get() = LoggingEnabled(true)

  override fun log(priority: Logger.Priority, @Inject tag: LoggingTag, message: String) {
    println("${logTag.value}: [${priority.name}] ${tag.value} $message")
  }
}

@JvmInline value class XposedLogTag(val value: String)
