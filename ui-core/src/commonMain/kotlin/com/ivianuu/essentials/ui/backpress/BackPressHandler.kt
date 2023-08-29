/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.backpress

import androidx.compose.runtime.staticCompositionLocalOf
import com.ivianuu.essentials.Disposable
import com.ivianuu.essentials.ui.AppUiDecorator
import com.ivianuu.injekt.Provide

interface BackPressHandler {
  val hasCallbacks: Boolean

  fun back()

  fun registerCallback(enabled: Boolean = true, callback: () -> Unit): BackPressCallbackHandle
}

interface BackPressCallbackHandle : Disposable {
  fun updateEnabled(enabled: Boolean)
}

val LocalBackPressHandler = staticCompositionLocalOf<BackPressHandler> { error("") }

fun interface BackPressHandlerProvider : AppUiDecorator

@Provide expect val backPressHandlerProvider: BackPressHandlerProvider