package com.ivianuu.essentials.accessibility

import android.app.Service
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.ivianuu.injekt.Given

typealias AccessibilityWindowManager = WindowManager

@Given
inline val Service.accessibilityWindowManager: AccessibilityWindowManager
    get() = getSystemService()!!
