/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import com.ivianuu.essentials.xposed.*
import com.ivianuu.injekt.*

@Provide class XposedLogger(private val config: XposedConfig) : Logger {
  override val isLoggingEnabled: LoggingEnabled
    get() = LoggingEnabled(true)

  override fun logMessage(priority: Logger.Priority, tag: String, message: String) {
    println("${config.modulePackageName}: [${priority.name}] $tag $message")
  }
}
