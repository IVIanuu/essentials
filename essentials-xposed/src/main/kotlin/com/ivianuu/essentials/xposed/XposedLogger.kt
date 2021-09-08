package com.ivianuu.essentials.xposed

import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.LoggingTag

class XposedLogger(private val appName: String) : Logger {
  override val isEnabled: Boolean
    get() = true

  override fun log(kind: Logger.Kind, tag: LoggingTag, message: String?, throwable: Throwable?) {
    println("$appName: [${kind.name}] $tag ${render(message, throwable)}")
  }

  private fun render(message: String?, throwable: Throwable?) = buildString {
    append(message.orEmpty())
    if (throwable != null) {
      append(" ")
      append(throwable)
    }
  }
}
