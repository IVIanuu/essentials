/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import com.ivianuu.injekt.Provide

fun interface UiRenderer<T> {
  operator fun invoke(x: T): String

  companion object {
    @Provide fun <T : Enum<T>> enum() = UiRenderer<T> { it.name }
    @Provide val string = UiRenderer<String> { it }
  }
}
