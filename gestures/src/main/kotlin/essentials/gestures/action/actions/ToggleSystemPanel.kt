package essentials.gestures.action.actions

import android.accessibilityservice.*
import android.accessibilityservice.AccessibilityService.*
import android.app.*
import android.view.accessibility.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.accessibility.*
import essentials.gestures.action.*
import injekt.*

@Provide object NotificationsActionId : ToggleSystemPanelActionId(
  "notifications",
  "accessibility_desc_notification_shade"
) {
  @Provide val action get() = Action(
    id = NotificationsActionId,
    title = "Notifications",
    permissions = accessibilityActionPermissions,
    icon = { Icon(Icons.Default.Notifications, null) }
  )
}

@Provide object QuickSettingsActionId : ToggleSystemPanelActionId(
  "quick_settings",
  "accessibility_desc_quick_settings"
) {
  @Provide val action get() = Action(
    id = QuickSettingsActionId,
    title = "Quick settings",
    permissions = accessibilityActionPermissions,
    icon = { Icon(Icons.Default.Settings, null) }
  )
}

abstract class ToggleSystemPanelActionId(
  value: String,
  val systemPanelResourceId: String
) : ActionId(value) {
  @Provide companion object {
    @Provide val accessibilityConfig: AccessibilityConfig
      get() = AccessibilityConfig(
        flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
            AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
      )

    @Provide suspend fun <@AddOn T : ToggleSystemPanelActionId> execute(
      id: T,
      appScope: Scope<AppScope>,
      context: Application,
      closeSystemDialogs: closeSystemDialogs,
      performAccessibilityAction: performGlobalAccessibilityAction
    ): ActionExecutorResult<T> {
      val targetState = catch {
        val service = appScope.scopeOfOrNull<AccessibilityScope>()!!.accessibilityService

        val systemUiContext = context.createPackageContext(
          "com.android.systemui", 0
        )

        val id = systemUiContext.resources.getIdentifier(
          id.systemPanelResourceId, "string", "com.android.systemui"
        )

        val notificationShadeDesc = systemUiContext.resources.getString(id)

        fun AccessibilityNodeInfo.isQuickSettingsPanelVisibleRecursive(): Boolean {
          if (packageName != "com.android.systemui") return false
          if (paneTitle == notificationShadeDesc) return true
          return (0 until childCount)
            .any { getChild(it).isQuickSettingsPanelVisibleRecursive() }
        }

        return@catch !service.rootInActiveWindow.isQuickSettingsPanelVisibleRecursive()
      }.getOrElse { true }

      if (targetState)
        performAccessibilityAction(GLOBAL_ACTION_QUICK_SETTINGS)
      else
        closeSystemDialogs()
    }
  }
}
