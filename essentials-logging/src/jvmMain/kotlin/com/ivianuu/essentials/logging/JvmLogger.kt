/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.logging

import java.io.PrintWriter
import java.io.StringWriter

actual fun Throwable.asLog(): String {
  val stringWriter = StringWriter(256)
  val printWriter = PrintWriter(stringWriter, false)
  printStackTrace(printWriter)
  printWriter.flush()
  return stringWriter.toString()
}
