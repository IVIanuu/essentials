/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common


interface CommonStrings {
  val cancel: String
  val close: String
  val ok: String
  val yes: String
  val no: String

  @Provide object Impl : CommonStrings {
    override val cancel = "Cancel"
    override val close = "Close"
    override val ok = "OK"
    override val yes = "Yes"
    override val no = "No"
  }
}
