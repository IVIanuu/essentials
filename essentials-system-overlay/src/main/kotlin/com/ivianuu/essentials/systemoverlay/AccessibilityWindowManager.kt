package com.ivianuu.essentials.systemoverlay

import android.app.Service
import android.view.WindowManager
import androidx.core.content.getSystemService
import com.ivianuu.injekt.Given

typealias AccessibilityWindowManager = WindowManager

@Given
inline val @Given Service.accessibilityWindowManager: AccessibilityWindowManager
    get() = getSystemService()!!
