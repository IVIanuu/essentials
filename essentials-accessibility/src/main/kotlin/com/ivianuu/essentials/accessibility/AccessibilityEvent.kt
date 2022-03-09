/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import com.ivianuu.essentials.coroutines.*

data class AccessibilityEvent(
  val type: Int,
  val packageName: String?,
  val className: String?,
  val isFullScreen: Boolean,
) {
  companion object {
    @Provide val accessibilityEvents = EventFlow<AccessibilityEvent>()
  }
}

typealias AndroidAccessibilityEvent = android.view.accessibility.AccessibilityEvent

