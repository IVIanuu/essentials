/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.backpress

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.ivianuu.essentials.Disposable
import com.ivianuu.injekt.Provide

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

  override fun registerCallback(enabled: Boolean, callback: () -> Unit): BackPressCallbackHandle =
    object : OnBackPressedCallback(enabled), BackPressCallbackHandle, Disposable {
      override fun handleOnBackPressed() {
        callback()
      }

      override fun updateEnabled(enabled: Boolean) {
        isEnabled = enabled
      }

      override fun dispose() {
        remove()
      }
    }.also { dispatcher.addCallback(it) }
}
