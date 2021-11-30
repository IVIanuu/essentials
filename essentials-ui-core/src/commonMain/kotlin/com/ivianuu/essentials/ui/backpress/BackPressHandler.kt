/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.backpress

import androidx.compose.runtime.staticCompositionLocalOf
import com.ivianuu.essentials.ui.UiDecorator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Disposable

interface BackPressHandler {
  val hasCallbacks: Boolean

  fun back()

  fun registerCallback(onBackPress: () -> Unit): Disposable
}

val LocalBackPressHandler = staticCompositionLocalOf<BackPressHandler> { error("") }

fun interface BackPressHandlerProvider : UiDecorator

@Provide expect val backPressHandlerProvider: BackPressHandlerProvider