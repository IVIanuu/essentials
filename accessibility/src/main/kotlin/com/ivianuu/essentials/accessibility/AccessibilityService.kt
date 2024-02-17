package com.ivianuu.essentials.accessibility

import android.accessibilityservice.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide class AccessibilityService(private val scopeManager: ScopeManager) {
  val events: Flow<AccessibilityEvent>
    get() = scopeManager.scopeOfOrNull<AccessibilityScope>()
      .flatMapLatest { it?.accessibilityService?.events ?: infiniteEmptyFlow() }

  suspend fun performGlobalAction(action: Int) {
    scopeManager.scopeOf<AccessibilityScope>().first().accessibilityService
      .performGlobalAction(action)
  }
}

typealias AndroidAccessibilityEvent = android.view.accessibility.AccessibilityEvent
typealias AndroidAccessibilityService = android.accessibilityservice.AccessibilityService

data class AccessibilityEvent(
  val type: Int,
  val packageName: String?,
  val className: String?,
  val isFullScreen: Boolean,
) {
  @Provide companion object {
    @Provide val accessibilityEvents = EventFlow<AccessibilityEvent>()
  }
}

data class AccessibilityConfig(
  val eventTypes: Int = 0,
  val flags: Int = 0,
  val packageNames: Set<String>? = null,
  val feedbackType: Int = AccessibilityServiceInfo.FEEDBACK_GENERIC,
  val notificationTimeout: Long = 0L,
) {
  @Provide companion object {
    @Provide val defaultConfigs get() = emptyList<AccessibilityConfig>()
  }
}
