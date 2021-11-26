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

package com.ivianuu.essentials.ui.insets

import android.view.View
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import com.ivianuu.injekt.Provide
import kotlin.math.max

@Provide actual val windowInsetsProvider = WindowInsetsProvider { content ->
  val ownerView = LocalView.current
  val density = LocalDensity.current
  var insets by remember { mutableStateOf(Insets()) }

  val insetsListener = remember {
    View.OnApplyWindowInsetsListener { _, rawInsets ->
      val currentInsets =
        WindowInsetsCompat.toWindowInsetsCompat(rawInsets, ownerView)

      val systemBarInsets = currentInsets.getInsets(WindowInsetsCompat.Type.systemBars())
      val imeInsets = currentInsets.getInsets(WindowInsetsCompat.Type.ime())

      with(density) {
        insets = Insets(
          left = max(systemBarInsets.left, imeInsets.left).toDp(),
          top = max(systemBarInsets.top, imeInsets.top).toDp(),
          right = max(systemBarInsets.right, imeInsets.right).toDp(),
          bottom = max(systemBarInsets.bottom, imeInsets.bottom).toDp(),
        )
      }

      return@OnApplyWindowInsetsListener rawInsets
    }
  }

  val attachListener = remember {
    object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        ownerView.requestApplyInsets()
      }

      override fun onViewDetachedFromWindow(v: View?) {
      }
    }
  }

  DisposableEffect(ownerView) {
    ownerView.setOnApplyWindowInsetsListener(insetsListener)
    ownerView.addOnAttachStateChangeListener(attachListener)

    if (ownerView.isAttachedToWindow) {
      ownerView.requestApplyInsets()
    }

    onDispose {
      ownerView.setOnApplyWindowInsetsListener(null)
      ownerView.removeOnAttachStateChangeListener(attachListener)
    }
  }

  InsetsProvider(insets, content)
}
