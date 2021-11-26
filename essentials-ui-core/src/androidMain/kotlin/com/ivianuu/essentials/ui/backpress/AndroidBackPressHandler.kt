/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.backpress

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Disposable

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
