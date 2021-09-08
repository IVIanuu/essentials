package com.ivianuu.essentials.logging

import com.ivianuu.injekt.Provide

@Provide class XposedLogger(private val logTag: XposedLogTag) : Logger {
  override val isEnabled: Boolean
    get() = true

  override fun log(kind: Logger.Kind, tag: LoggingTag, message: String?, throwable: Throwable?) {
    println("$logTag: [${kind.name}] $tag ${render(message, throwable)}")
  }

  private fun render(message: String?, throwable: Throwable?) = buildString {
    append(message.orEmpty())
    if (throwable != null) {
      append(" ")
      append(throwable)
    }
  }
}

typealias XposedLogTag = String
