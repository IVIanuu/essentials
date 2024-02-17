/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import android.accessibilityservice.*
import android.view.*
import androidx.core.content.*
import com.ivianuu.injekt.*

@Tag annotation class AccessibilityWindowManagerTag {
  @Provide companion object {
    @Provide inline fun windowManager(service: AccessibilityService): AccessibilityWindowManager =
      service.getSystemService()!!
  }
}
typealias AccessibilityWindowManager = @AccessibilityWindowManagerTag WindowManager
