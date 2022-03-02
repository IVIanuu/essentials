/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.insets

import android.view.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.core.view.*
import com.ivianuu.injekt.*
import kotlin.math.*

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
