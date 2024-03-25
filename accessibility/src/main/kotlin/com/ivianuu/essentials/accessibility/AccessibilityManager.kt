package com.ivianuu.essentials.accessibility

import android.accessibilityservice.*
import androidx.compose.runtime.*
import com.hoc081098.flowext.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

data object AccessibilityScope

val Scope<*>.accessibilityService: EsAccessibilityService
  get() = service()

@Provide class AccessibilityManager(private val appScope: Scope<AppScope>) {
  val events: Flow<AccessibilityEvent>
    get() = snapshotFlow { appScope.scopeOfOrNull<AccessibilityScope>() }
      .flatMapLatest { it?.accessibilityService?.events ?: neverFlow() }

  suspend fun performGlobalAction(action: Int) {
    appScope.scopeOf<AccessibilityScope>().first().accessibilityService
      .performGlobalAction(action)
  }
}

typealias AndroidAccessibilityEvent = android.view.accessibility.AccessibilityEvent

data class AccessibilityEvent(
  val type: Int,
  val packageName: String?,
  val className: String?,
  val isFullScreen: Boolean,
)

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
