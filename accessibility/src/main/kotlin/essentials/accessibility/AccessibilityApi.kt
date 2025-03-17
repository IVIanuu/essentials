package essentials.accessibility

import android.accessibilityservice.*
import androidx.compose.runtime.*
import com.hoc081098.flowext.*
import essentials.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide data object AccessibilityScope : ChildScopeMarker<AccessibilityScope, AppScope>

val Scope<*>.accessibilityService: EsAccessibilityService
  get() = service()

@Provide fun accessibilityEvents(appScope: Scope<AppScope>): Flow<AccessibilityEvent> =
  snapshotFlow { appScope.scopeOfOrNull<AccessibilityScope>() }
    .flatMapLatest { it?.accessibilityService?.events ?: neverFlow() }

@Tag typealias performGlobalAccessibilityAction = suspend (Int) -> Unit
@Provide fun performGlobalAccessibilityAction(
  appScope: Scope<AppScope>
): performGlobalAccessibilityAction = {
  appScope.scopeOf<AccessibilityScope>().first().accessibilityService
    .performGlobalAction(it)
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
