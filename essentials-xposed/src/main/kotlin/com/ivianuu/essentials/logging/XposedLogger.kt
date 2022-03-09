/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging


@Provide class XposedLogger(private val logTag: XposedLogTag) : Logger {
  override val isEnabled: LoggingEnabled
    get() = LoggingEnabled(true)

  override fun log(message: String, priority: Logger.Priority, tag: LoggingTag) {
    println("${logTag.value}: [${priority.name}] ${tag.value} $message")
  }
}

@JvmInline value class XposedLogTag(val value: String)
