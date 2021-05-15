package com.ivianuu.essentials

internal actual open class ControlException : Throwable() {
  override fun fillInStackTrace(): Throwable = this
}
