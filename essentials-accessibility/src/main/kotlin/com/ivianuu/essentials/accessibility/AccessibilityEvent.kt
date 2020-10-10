package com.ivianuu.essentials.accessibility

data class AccessibilityEvent(
    val type: Int,
    val packageName: String?,
    val className: String?,
    val isFullScreen: Boolean,
)

typealias AndroidAccessibilityEvent = android.view.accessibility.AccessibilityEvent