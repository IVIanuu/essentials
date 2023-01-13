/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import com.ivianuu.injekt.Provide

@Provide class XposedLogger(private val logTag: XposedLogTag) : Logger {
  override val isLoggingEnabled: LoggingEnabled
    get() = LoggingEnabled(true)

  override fun logMessage(tag: String, message: String, priority: Logger.Priority) {
    println("${logTag.value}: [${priority.name}] $tag $message")
  }
}

@JvmInline value class XposedLogTag(val value: String)
