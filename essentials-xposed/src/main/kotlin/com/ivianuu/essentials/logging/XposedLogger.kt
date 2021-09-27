package com.ivianuu.essentials.logging

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

@Provide class XposedLogger(private val logTag: XposedLogTag) : Logger {
  override val isEnabled: Boolean
    get() = true

  override fun log(priority: Logger.Priority, @Inject tag: LoggingTag, message: String) {
    println("$logTag: [${priority.name}] $tag $message")
  }
}

typealias XposedLogTag = String
