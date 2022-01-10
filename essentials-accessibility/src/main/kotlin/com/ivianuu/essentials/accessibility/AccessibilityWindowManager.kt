/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import android.app.*
import android.view.*
import androidx.core.content.*
import com.ivianuu.injekt.*

@Tag annotation class AccessibilityWindowManagerTag {
  companion object {
    @Provide inline fun windowManager(service: Service): AccessibilityWindowManager =
      service.getSystemService()!!
  }
}
typealias AccessibilityWindowManager = @AccessibilityWindowManagerTag WindowManager
