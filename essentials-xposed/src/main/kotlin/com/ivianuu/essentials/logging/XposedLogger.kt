package com.ivianuu.essentials.logging

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

@Provide class XposedLogger(private val logTag: XposedLogTag) : Logger {
  override val isEnabled: Boolean
    get() = true

  override fun log(kind: Logger.Kind, @Inject tag: LoggingTag, message: String) {
    println("$logTag: [${kind.name}] $tag $message")
  }
}

typealias XposedLogTag = String
