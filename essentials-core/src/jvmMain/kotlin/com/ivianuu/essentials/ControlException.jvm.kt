/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials

actual open class ControlException : Throwable() {
  override fun fillInStackTrace(): Throwable = this
}
