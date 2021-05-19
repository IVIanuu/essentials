package com.ivianuu.essentials.accessibility

import android.app.*
import android.view.*
import androidx.core.content.*
import com.ivianuu.injekt.*

typealias AccessibilityWindowManager = WindowManager

@Provide inline val Service.accessibilityWindowManager: AccessibilityWindowManager
  get() = getSystemService()!!
