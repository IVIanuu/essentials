/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.*
import com.ivianuu.injekt.*

@Stable fun interface UiRenderer<in T> {
  fun render(x: T): String

  @Provide companion object {
    @Provide fun <T : Enum<T>> enum() = UiRenderer<T> { it.name }
    @Provide val string = UiRenderer<String> { it }
    @Provide fun <T> collection(
      elementRenderer: UiRenderer<T>
    ) = UiRenderer<Collection<T>> { values ->
      values.joinToString(", ") { elementRenderer.render(it) }
    }
  }
}
