/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.backpress

import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Provide actual val backPressHandlerProvider = BackPressHandlerProvider { content ->
  val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
  CompositionLocalProvider(
    LocalBackPressHandler provides remember(dispatcher) {
      AndroidBackPressHandler(dispatcher)
    },
    content = content
  )
}

class AndroidBackPressHandler(
  private val dispatcher: OnBackPressedDispatcher
) : BackPressHandler {
  override val hasCallbacks: Boolean
    get() = dispatcher.hasEnabledCallbacks()

  override fun back() {
    dispatcher.onBackPressed()
  }

  override fun registerCallback(onBackPress: () -> Unit): Disposable =
    object : OnBackPressedCallback(true), Disposable {
      override fun handleOnBackPressed() {
        onBackPress()
      }

      override fun dispose() {
        remove()
      }
    }.also { dispatcher.addCallback(it) }
}
