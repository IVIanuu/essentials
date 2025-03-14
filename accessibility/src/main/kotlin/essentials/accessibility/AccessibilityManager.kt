package essentials.accessibility

import android.accessibilityservice.*
import androidx.compose.runtime.*
import com.hoc081098.flowext.*
import essentials.*
import injekt.*
import kotlinx.coroutines.flow.*

data object AccessibilityScope

val Scope<*>.accessibilityService: EsAccessibilityService
  get() = service()

fun accessibilityEvents(scope: Scope<*> = inject): Flow<AccessibilityEvent> =
  snapshotFlow { scope.scopeOfOrNull<AccessibilityScope>() }
    .flatMapLatest { it?.accessibilityService?.events ?: neverFlow() }

suspend fun performGlobalAccessibilityAction(
  action: Int,
  scope: Scope<AppScope> = inject
) {
  scope.scopeOf<AccessibilityScope>().first().accessibilityService
    .performGlobalAction(action)
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
